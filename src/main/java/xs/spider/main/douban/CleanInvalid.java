package xs.spider.main.douban;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import xs.spider.base.bean.PageBean;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.pager.PageContext;
import xs.spider.base.util.ApplicationContextHandle;
import xs.spider.base.util.DateUtil;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.Util;
import xs.spider.base.util.http.HttpClientUtil;
import xs.spider.base.util.http.HttpReqBean;
import xs.spider.base.util.http.HttpRespBean;
import xs.spider.main.Init;
import xs.spider.work.bean.TitleInfo;
import xs.spider.work.dao.TitleInfoDao;

import java.util.List;
import java.util.Random;

/**
 * Created by xs on 2017/4/13.
 */
public class CleanInvalid {
    static {
        Init.init();
    }
    public static int cnt = 0;
    private static TitleInfoDao titleInfoDao = (TitleInfoDao) ApplicationContextHandle.getBean("titleInfoDao");
    private static Logger log = LogUtil.getLogger(CleanInvalid.class);
    public static void main(String[] args) {
        int pagenum = 1;
        int total = titleInfoDao.getCount("select id from title_info", null);
        BasicCookieStore cookieStore = new BasicCookieStore();
        try(CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build()) {
            DoubanHttpUtil.dologin(httpclient);
            while (true) {
                PageContext.initPage(pagenum, 50);
                PageBean page = titleInfoDao.getPage("SELECT * from title_info WHERE isValid=1 order by id asc", null);
                List<TitleInfo> list = page.getData();
                if (list == null || list.isEmpty()) {
                    log.info("结束了..");
                    return;
                }
                Random ran = new Random();
                for (TitleInfo info : list) {
                    cnt++;
                    doClean(httpclient, info, ran);
                }
//                pagenum++;
            }
        } catch (Exception e) {
        }
    }

    public static void doClean(CloseableHttpClient httpclient, TitleInfo info, Random ran) throws Exception {
        Thread.sleep(1000 + (ran.nextInt(3000)-1000));
        ResultInfo isValid = checkUrl(httpclient, info.getUrl(), 0);
        if (isValid.getCode()!=1) {
            info.setIsValid(0);
            log.info("页面失效,item=" + cnt);
            titleInfoDao.update(info, false);
        } else {
            if(!Util.isBlank(isValid.getMessage())) {
                info.setContent(isValid.getMessage());
            }
            info.setIsValid(2);
            try {
                if (!Util.isBlank(isValid.getData())) {
                    info.setCreateTime(DateUtil.parseStringToDate(isValid.getData().toString(),
                            DateUtil.C_YYYY_MM_DD_HH_MM_SS));
                }
                int i = titleInfoDao.update(info, false);
                if (i==-1) {
                    info.setIsValid(0);
                    info.setContent("失效：重复标题" + ran.nextInt(Integer.MAX_VALUE));
                    titleInfoDao.update(info, false);
                }
            } catch (Exception e) {
            }
        }
    }

    public static ResultInfo checkUrl(CloseableHttpClient httpClient, String url, int retry) throws Exception {
        if (Util.isBlank(url)) {
            return new ResultInfo(-1, "");
        }
        HttpReqBean req = new HttpReqBean(url, null, 0, null, null);
        HttpRespBean resp = HttpClientUtil.doGet(req, httpClient, null);
        ResultInfo ri = new ResultInfo(1, "");
        if (resp.getCode() == 1) {
            try {
                Document page = Jsoup.parse(resp.getResponseBody());
                String title = "";
                try {
                    title = page.select("title").first().text();
                } catch (Exception e) {

                }
                System.out.println(title);
                if (title.equals("页面不存在") || title.equals("豆瓣")) {
                    return new ResultInfo(-1, "");
                }
                if (title.endsWith("...")) {
                    try {
                        title = page.select(".infobox").first().select("tr").get(1).select(".tablecc").first().text();
                    } catch (Exception e) {
                        log.error(e);
                        title = "";
                    }
                    if (!Util.isBlank(title)) {
                        title = title.replace("标题：", "");
                    }
                }
                String createTime = null;
                try {
                    createTime = page.select(".topic-doc").first().child(0).select(".color-green").first().text();
                } catch (Exception e) {
                    log.error(e);
                }
                ri.setCodeAndMsg(1, title);
                if (!Util.isBlank(createTime)) {
                    ri.setData(createTime);
                }
                return ri;
            } catch (Exception e) {
                log.error("异常", e);
            }
        } else {
            if (resp.getMessage().indexOf("reqErr:") != -1) {
                Integer status = Integer.parseInt(resp.getMessage().replace("reqErr:", ""));
                if (status.equals(404)) {
                    return new ResultInfo(-1, "");
                } else {
                    if (retry >3) {
                        System.out.println("请求了"+cnt+"次后停下来了");
                        System.exit(1);
                    } else {
                        DoubanHttpUtil.dologin(httpClient);
                        return checkUrl(httpClient, url, retry + 1);
                    }
                }
            }
        }
        return new ResultInfo(1, "");
    }
}
