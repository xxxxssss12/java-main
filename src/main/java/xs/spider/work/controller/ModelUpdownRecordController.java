package xs.spider.work.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.spider.base.bean.PageBean;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.pager.PageContext;
import xs.spider.base.util.DateUtil;
import xs.spider.base.util.TypeConvUtil;
import xs.spider.base.util.Util;
import xs.spider.work.bean.ModelDetail;
import xs.spider.work.bean.ModelUpdownRecord;
import xs.spider.work.dao.ModelUpdownRecordDao;
import xs.spider.work.service.ModelAllServiceImpl;
import xs.spider.work.service.ModelDetailServiceImpl;
import xs.spider.work.service.ModelUpdownRecordServiceImpl;
import xs.spider.work.shiro.CurrentUserHelper;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上下场记录
 * Created by xs on 2017/10/29
 */
@RestController
@RequestMapping("/modelUpdown")
public class ModelUpdownRecordController {
    @Resource
    private ModelUpdownRecordServiceImpl modelUpdownRecordService;
    @Resource
    private ModelAllServiceImpl modelAllService;

    @RequestMapping("/getByModelId")
    public JSONObject getByModelId(Integer modelId) {
        if (modelId == null) return JSON.parseObject(ResultInfo.buildFail("缺少必要参数").toString());
        ModelUpdownRecord record = new ModelUpdownRecord();
        record.setModelAllId(modelId);
        PageContext.initPage(1, 100);
        PageBean<ModelUpdownRecord> updownRecordPage = modelUpdownRecordService.getPage(record);
        return TypeConvUtil.convertPageToLiger(updownRecordPage);
    }

    @RequestMapping("/add")
    public ResultInfo add(
            Integer type,   // 0：下场;1：上场
            Integer modelAllId,
            String nowLocation,
            String actionTime,
            String remark) {
        ResultInfo ri = checkAdd(type, modelAllId, nowLocation, actionTime);
        if (ri.getCode() <= 0) return ri;
        ModelUpdownRecord record = new ModelUpdownRecord();
        record.setModelAllId(modelAllId);
        record.setNowLocation(nowLocation);
        record.setActionTime(DateUtil.parseStringToDate(actionTime, DateUtil.C_YYYY_MM_DD_HH_MM_SS));
        record.setRemark(remark);
        record.setCreateTime(new Date());
        record.setCreateUser(CurrentUserHelper.getCurrentUsername());
        modelUpdownRecordService.save(record);
        return ResultInfo.build();
    }
    @RequestMapping("/batchUpdate")
    public ResultInfo batchUpdate(String modelDetailListJson) {
        if (Util.isBlank(modelDetailListJson)) {
            return ResultInfo.buildFail("缺少必要参数");
        }
        List<ModelUpdownRecord> recordList = JSON.parseArray(modelDetailListJson, ModelUpdownRecord.class);
        for (ModelUpdownRecord record : recordList) {
            modelUpdownRecordService.update(record, false);
        }
        return ResultInfo.build();
    }

    @RequestMapping("/delete")
    public ResultInfo delete(Integer id) {
        if (Util.isBlank(id)) return ResultInfo.buildFail("缺少必要参数");
        modelUpdownRecordService.delete(id);
        return ResultInfo.build();
    }
    private ResultInfo checkAdd(Integer type, Integer modelAllId, String nowLocation, String actionTime) {
        if (type == null) {
            return ResultInfo.buildFail("请标注是上场还是下场");
        }
        if (modelAllId == null) {
            return ResultInfo.buildFail("缺少参数");
        }
        if (Util.isBlank(nowLocation)) {
            return ResultInfo.buildFail("当前位置不能为空");
        }
        if (Util.isBlank(actionTime)) {
            return ResultInfo.buildFail("时间不能为空");
        }
        return ResultInfo.build();
    }
}
