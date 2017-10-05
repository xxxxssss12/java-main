package xs.spider.work.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xs.spider.base.bean.PageBean;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.config.ConfigProvider;
import xs.spider.base.pager.PageContext;
import xs.spider.base.util.*;
import xs.spider.base.util.esutils.ElasticSearchUtil;
import xs.spider.base.util.http.HttpClientManager;
import xs.spider.base.util.http.HttpClientUtil;
import xs.spider.base.util.http.HttpReqBean;
import xs.spider.base.util.http.HttpRespBean;
import xs.spider.main.douban.DoubanMain;
import xs.spider.work.bean.ContentDetail;
import xs.spider.work.bean.EsDetailBean;
import xs.spider.work.bean.Pic;
import xs.spider.work.bean.TitleInfo;
import xs.spider.work.dao.ContentDetailDao;
import xs.spider.work.dao.PicDao;
import xs.spider.work.dao.TitleInfoDao;

import java.util.*;

/**
 * Created by xs on 2017/9/30.
 */
@Service
public class DoubanService {
    private static Random ran = new Random();
    private static Logger log = LogUtil.getLogger(DoubanMain.class);

    private static String PIC_FILE_BASE_PATH = ConfigProvider.get("pic.file.root.path");
    @Autowired
    private TitleInfoDao titleInfoDao;
    @Autowired
    private PicDao picDao;
    @Autowired
    private ContentDetailDao contentDetailDao;

    public int saveTitle(Element row, Integer pagenum) {
        TitleInfo titleInfo = new TitleInfo();
        titleInfo.setContent(row.child(0).select("a").first().attr("title"));
        titleInfo.setUrl(row.child(0).select("a").first().attr("href"));
        titleInfo.setPagenum(pagenum+1);
        titleInfo.setUpdateTime(new Date());
        titleInfo.setCreateTime(new Date());
        titleInfo.setIsValid(1);
        titleInfo.setTime(DateUtil.parseStringToDate(row.select(".td-time").first().attr("title"),
                DateUtil.C_YYYY_MM_DD_HH_MM_SS));
        try {
            return titleInfoDao.save(titleInfo);
        } catch (Exception e) {
            log.info("房源已存在--" + JSON.toJSONString(titleInfo));
        }
        return -1;
    }

    public void saveContentAndPic(int titleId) {
        TitleInfo titleInfo = titleInfoDao.get(titleId);
        if (titleInfo == null) {
            log.info("没找到title----titleId=" + titleId);
            return;
        }
        Element contentNode = getContent(titleInfo);
        saveDetailOrInvalidTitle(contentNode, titleInfo);
    }

    private void saveDetailOrInvalidTitle(Element contentNode, TitleInfo titleInfo) {
        if (contentNode == null) {
            titleInfo.setIsValid(0);
            titleInfo.setUpdateTime(new Date());
            titleInfoDao.update(titleInfo, false);
            return;
        }
        ContentDetail detail = contentDetailDao.getByTitleId(titleInfo.getId());
        if (detail == null) detail = new ContentDetail();
        Elements contentEle = contentNode.select("p");
        if (contentEle != null && !contentEle.isEmpty()) {
            String content = contentEle.first().text();
            detail.setContent(content);
        }
        detail.setTitleid(titleInfo.getId());
        if (detail.getCreatetime() == null) detail.setCreatetime(new Date());
        detail.setUpdateTime(new Date());
        if (detail.getId() == null) contentDetailDao.save(detail);
        else contentDetailDao.update(detail, false);
        List<Pic> picList = getPicList(contentNode);
        if (picList!=null && !picList.isEmpty()) {
            savePics(titleInfo, picList);
        }
        saveToEs(titleInfo, detail, picList);
    }
    public void importAllToEs() {
        int page = 1;
        int pagesize = 1000;
        List<TitleInfo> titleInfoList = null;
        do {
            PageContext.initPage(page,pagesize);
            PageBean<TitleInfo> pageBean = titleInfoDao.getPage("select * from title_info", null);
            page++;
            if (pageBean != null && pageBean.getData() != null) titleInfoList = pageBean.getData();
            if (titleInfoList != null && !titleInfoList.isEmpty()) {
                for (TitleInfo info : titleInfoList) {
                    ContentDetail detail = contentDetailDao.getByTitleId(info.getId());
                    if (detail == null) {
                        LogUtil.info(getClass(), "info=" + JSON.toJSONString(info) + ";detail=null");
                        continue;
                    }
                    List<Pic> picList = picDao.getByTitleId(info.getId());
                    saveToEs(info, detail, picList);
                }
            }
        } while (titleInfoList != null && !titleInfoList.isEmpty());
    }
    public void saveToEs(TitleInfo titleInfo, ContentDetail detail, List<Pic> picList) {
        EsDetailBean bean = new EsDetailBean();
        bean.setTitleId(titleInfo.getId());
        bean.setTitle(titleInfo.getContent());
        bean.setTime(DateUtil.parseDateToString(titleInfo.getTime(), DateUtil.C_YYYY_MM_DD_HH_MM_SS));
        bean.setContentId(detail.getId());
        bean.setIsValid(titleInfo.getIsValid());
        bean.setContent(detail.getContent());
        bean.setPicInfo(getPicInfoByList(picList));
        bean.setTitleUrl(titleInfo.getUrl());
        bean.setCreateTimestamp(titleInfo.getTime().getTime());
        bean.setIsHavePic(picList==null||picList.isEmpty()?0:1);
        try {
            ResultInfo ri = ElasticSearchUtil.insert("douban", "zufang", titleInfo.getId().toString(), JSON.parseObject(JSON.toJSONString(bean)));
            LogUtil.info(getClass(), titleInfo.getId() + "..saveToEs success:" + JSON.toJSONString(ri));
        } catch (Exception e) {
            LogUtil.error(getClass(), "saveToEs error:" + ExceptionWrite.get(e));
        }
    }

    private JSONArray getPicInfoByList(List<Pic> picList) {
        JSONArray array = new JSONArray();
        if (picList == null || picList.isEmpty()) return array;
        for (Pic pic : picList) {
            JSONObject obj = new JSONObject();
            obj.put("id", pic.getId());
            obj.put("url", pic.getUrl());
            array.add(obj);
        }
        return array;
    }

    private void savePics(TitleInfo titleInfo, List<Pic> picList) {
        if (picList == null || picList.isEmpty() || titleInfo == null || titleInfo.getId() == null) return;
        titleInfoDao.deleteTitlePic(titleInfo.getId());
        for (Pic pic : picList) {
            pic.setBasepath(PIC_FILE_BASE_PATH);
//            pic.setLocation(savePic(pic.getUrl()));
            pic.setCreatetime(new Date());
            Integer id = picDao.save(pic);
            pic.setId(id);
            titleInfoDao.saveTitlePic(titleInfo.getId(), id);
        }
    }

    private String savePic(String url) {
        String tempStr = MD5Util.MD5(url);
        String filePath = tempStr.substring(0,2) + "/" + tempStr.substring(2,4) + "/" + tempStr + url.substring(url.lastIndexOf("."));
        HttpClientUtil.getStaticToFile(url, PIC_FILE_BASE_PATH + filePath);
        return filePath;
    }

    private List<Pic> getPicList(Element contentNode) {
        Elements picNodes = contentNode.select(".topic-figure");
        if (picNodes == null || picNodes.isEmpty()) return null;
        List<Pic> picList = new ArrayList<>();
        for (Element picNode : picNodes) {
            Pic pic = new Pic();
            pic.setUrl(picNode.select("img").first().attr("src"));
            Elements span = picNode.select("span");
            if (span != null && !span.isEmpty()) {
                pic.setPicName(span.first().text());
            }
            picList.add(pic);
        }
        return picList;
    }

    private Element getContent(TitleInfo titleInfo) {
        HttpClient httpClient = HttpClientManager.getHttpClient();
        try {
            Thread.sleep(1000 + (ran.nextInt(1000)-500));
            HttpReqBean reqBean = new HttpReqBean(titleInfo.getUrl(), null, 0, null, null);
            HttpRespBean respBean = HttpClientUtil.doGet(reqBean, httpClient, null);
            if (respBean != null && respBean.getCode() == 1) {
                Document doc = Jsoup.parse(respBean.getResponseBody());
                Element content = doc.select("#content").first();
                if (content == null) {
                    LogUtil.error(getClass(), "getContent content=null");
                    return null;
                }
                String title = content.select("h1").first().text();
                Element topicDoc = content.select(".topic-doc").first();
                String time = topicDoc.select(".color-green").text();
                Date createtime = null;
                if (!Util.isBlank(time)) createtime = DateUtil.parseStringToDate(time, DateUtil.C_YYYY_MM_DD_HH_MM_SS);
                Elements infoBox = topicDoc.select(".infobox");
                if (infoBox != null && !infoBox.isEmpty()) {
                    title = infoBox.first().select(".tablecc").text().replace("标题：", "");
                }
                titleInfo.setContent(title);
                titleInfo.setTime(createtime);
                titleInfo.setUpdateTime(new Date());
                titleInfoDao.update(titleInfo, false);
                return topicDoc.select(".topic-content").first();
            } else {
                LogUtil.error(getClass(), "getContent respBean=" + JSON.toJSONString(respBean));
            }
        } catch (Exception e) {
            LogUtil.error(getClass(), "getContent titleInfo=" +JSON.toJSONString(titleInfo) + "error:" + ExceptionWrite.get(e));
        }
        return null;

    }
}
