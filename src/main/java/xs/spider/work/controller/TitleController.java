package xs.spider.work.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.spider.base.bean.PageBean;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.pager.PageContext;
import xs.spider.base.util.ExceptionWrite;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.Util;
import xs.spider.base.util.esutils.ElasticSearchUtil;
import xs.spider.work.bean.ContentDetail;
import xs.spider.work.bean.Pic;
import xs.spider.work.bean.SubwayStation;
import xs.spider.work.bean.TitleInfo;
import xs.spider.work.dao.ContentDetailDao;
import xs.spider.work.dao.PicDao;
import xs.spider.work.dao.TitleInfoDao;
import xs.spider.work.service.DoubanService;
import xs.spider.work.service.SubwayService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by xs on 2017/4/12.
 */
@RestController
@RequestMapping("/title")
public class TitleController {
    @Resource
    private TitleInfoDao titleInfoDao;
    @Resource
    private SubwayService subwayService;
    @Resource
    private ContentDetailDao contentDetailDao;
    @Resource
    private DoubanService doubanService;
    @Resource
    private PicDao picDao;
    @RequestMapping("/getByPage")
    public PageBean getByPage(String title) throws Exception {
        return titleInfoDao.getPage(title);
    }
    @RequestMapping("/remove")
    public ResultInfo remove(Integer id, Integer isDelete) throws Exception {
        if (id == null) return new ResultInfo(-1, "缺少参数");
        TitleInfo info = titleInfoDao.get(id);
        if (isDelete!=null && isDelete==1) {
            info.setIsValid(0);
        } else {
            info.setIsValid(-1);
        }
        info.setUpdateTime(new Date());
        titleInfoDao.update(info, false);
        ResultInfo ri = getSingle(id);
        if (ri.getCode() == 1) {
            JSONObject obj = (JSONObject) ri.getData();
            doubanService.saveToEs(obj.getObject("title", TitleInfo.class),
                    obj.getObject("content", ContentDetail.class),
                    convertJSONArrayToPicList(obj.getJSONArray("pics")));
        }
        return new ResultInfo(1,"成功");
    }

    private List<Pic> convertJSONArrayToPicList(JSONArray pics) {
        List<Pic> picList = new ArrayList<>();
        if (pics == null || pics.isEmpty()) return picList;
        for (int i=0;i<pics.size(); i++) {
            Pic pic = pics.getObject(1, Pic.class);
            picList.add(pic);
        }
        return picList;
    }


    @RequestMapping("/getEsByPage")
    public PageBean getEsByPage(Integer stationId, String searchText) throws Exception {
        SubwayStation station = subwayService.getStationById(stationId);
        PageBean<Object> bean = searchEs(station, searchText);
        return bean;
    }

    /**
     *
    {
        "query": {
        "bool" {"must":[{"match": {
         "content" : "惠新"
         }}]}

        },
        "sort" : [{
            "createTimestamp": {"order": "desc"}
        }],
        "from": 0,
        "size": 20,
        "highlight" : {
            "pre_tags" : [ "<span style='color:red'>" ],
            "post_tags" : [ "</span>" ],
            "fields" : {
                "content" : {},
                "title" :{}
            }
        }
    }
     * @param station
     */
    private PageBean searchEs(SubwayStation station, String searchText) {
        JSONObject search = new JSONObject();
        JSONObject query = new JSONObject();
        JSONObject bool = new JSONObject();
        JSONArray must = new JSONArray();
        JSONObject boolMatch1 = new JSONObject();
        JSONObject boolMatch2 = new JSONObject();
        JSONObject validMatch = new JSONObject();
        validMatch.put("isValid", 1);
        JSONObject contentMatch = new JSONObject();
        JSONArray sort = new JSONArray();
        JSONObject sortCreateTimestamp = new JSONObject();
        JSONObject highlight = new JSONObject();
        JSONObject fields = new JSONObject();
        search.put("query", query);
        search.put("sort", sort);
        search.put("from", PageContext.getStartNum());
        search.put("size", PageContext.getPageSize());
        search.put("highlight", highlight);

        query.put("bool", bool);
        bool.put("must", must);
        validMatch.put("isValid", 1);
        must.add(boolMatch2);
        boolMatch1.put("match_phrase", contentMatch);
        boolMatch2.put("match", validMatch);
        if (station != null || !Util.isBlank(searchText)) {
            contentMatch.put("content", station==null?searchText: station.getName());
            must.add(boolMatch1);
        }
        JSONObject sort1 = new JSONObject();
        sort1.put("createTimestamp", sortCreateTimestamp);
        sortCreateTimestamp.put("order", "desc");
        sort.add(sort1);
        highlight.put("pre_tags", Arrays.asList("<span style='color:red'>"));
        highlight.put("post_tags", Arrays.asList("</span>"));
        highlight.put("fields", fields);
        fields.put("content", new JSONObject());
        fields.put("title", new JSONObject());

        try {
            LogUtil.info(getClass(), "查询参数：search=" + search.toJSONString());
            ResultInfo ri = ElasticSearchUtil.search("douban", null, null, search);
            if (ri == null || ri.getCode() <= 0) return new PageBean(-1);
            String jsonResp = Util.null2string(ri.getData());
            JSONObject resp = JSON.parseObject(jsonResp);
            JSONObject hits = resp.getJSONObject("hits");
            if (hits == null || hits.isEmpty()) return new PageBean(-2);
            PageBean<Object> bean = new PageBean<>();
            bean.setCode(1);
            bean.setTotal(hits.getInteger("total"));
            bean.setPageNum(PageContext.getPageNum());
            bean.setPageSize(PageContext.getPageSize());
            bean.setData(hits.getJSONArray("hits"));
            return bean;
        } catch (Exception e) {
            LogUtil.error(getClass(), "station=" + JSON.toJSONString(station) + ";searchText=" +searchText + ";" + ExceptionWrite.get(e));
            return new PageBean(-3);
        }
    }

    private ResultInfo getSingle(Integer titleId) {
        if (titleId == null) return new ResultInfo(-1, "缺少参数");
        JSONObject obj = new JSONObject();
        TitleInfo titleInfo = titleInfoDao.get(titleId);
        if (titleInfo == null) return new ResultInfo(-1, "not found");
        obj.put("title", titleInfo);
        obj.put("content", contentDetailDao.getByTitleId(titleId));
        obj.put("pics", picDao.getByTitleId(titleId));
        return new ResultInfo(1, "success", obj);
    }
}
