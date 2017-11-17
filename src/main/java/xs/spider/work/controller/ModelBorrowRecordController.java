package xs.spider.work.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.spider.base.bean.PageBean;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.pager.PageContext;
import xs.spider.base.util.DateUtil;
import xs.spider.base.util.TypeConvUtil;
import xs.spider.base.util.Util;
import xs.spider.work.bean.ModelAll;
import xs.spider.work.bean.ModelBorrowRecord;
import xs.spider.work.bean.ModelUpdownRecord;
import xs.spider.work.service.ModelAllServiceImpl;
import xs.spider.work.service.ModelBorrowRecordServiceImpl;
import xs.spider.work.service.ModelUpdownRecordServiceImpl;
import xs.spider.work.shiro.CurrentUserHelper;

import javax.annotation.Resource;
import javax.xml.transform.Result;
import java.util.Date;
import java.util.List;

/**
 * 借出／归还记录
 * Created by xs on 2017/10/29
 */
@RestController
@RequestMapping("/modelBorrow")
public class ModelBorrowRecordController {
    @Resource
    private ModelBorrowRecordServiceImpl modelBorrowRecordService;
    @Resource
    private ModelAllServiceImpl modelAllService;

    @RequestMapping("/getByModelId")
    public JSONObject getByModelId(Integer modelId) {
        if (modelId == null) return JSON.parseObject(ResultInfo.buildFail("缺少必要参数").toString());
        ModelBorrowRecord record = new ModelBorrowRecord();
        record.setModelAllId(modelId);
        PageContext.initPage(1, 100);
        PageBean<ModelBorrowRecord> updownRecordPage = modelBorrowRecordService.getPage(record, " borrowTime desc, id desc ");
        return TypeConvUtil.convertPageToLiger(updownRecordPage);
    }

    @RequestMapping("/add")
    public ResultInfo add(
            Integer modelAllId,
            String borrowCompany,   // 借出公司
            String approvalPerson,  // 负责人
            String managePerson,    // 审批人（领导）
            String borrowCustomer,  // 借出给谁
            String borrowTime,        // 借出时间
            String returnTime,        // 归还时间
            Integer isReturn,       // 是否归还（0否1是）
            String remark) {
        ResultInfo ri = checkAdd(modelAllId, borrowCustomer, borrowCompany, approvalPerson, borrowTime);
        if (ri.getCode() <= 0) return ri;
        ModelAll all = modelAllService.get(modelAllId);
        ModelBorrowRecord record = new ModelBorrowRecord();
        record.setModelAllId(modelAllId);
        record.setBorrowCustomer(borrowCustomer);
        record.setBorrowCompany(borrowCompany);
        record.setBorrowCustomer(borrowCustomer);
        record.setApprovalPerson(approvalPerson);
        record.setManagePerson(managePerson);
        record.setBorrowTime(DateUtil.parseStringToDate(borrowTime, DateUtil.C_YYYY_MM_DD_HH_MM_SS));
        record.setPreLocation(all.getStoreLocation());
        if (record.getReturnTime() == null)
            record.setReturnTime(DateUtil.parseStringToDate(returnTime, DateUtil.C_YYYY_MM_DD_HH_MM_SS));
        if (isReturn == null) isReturn = 0;
        record.setIsReturn(isReturn);
        record.setRemark(remark);
        record.setCreateTime(new Date());
        record.setCreateUser(CurrentUserHelper.getCurrentUsername());
        modelBorrowRecordService.save(record);
        modelAllService.refreshLocation(modelAllId);
        return ResultInfo.build();
    }
    @RequestMapping("/batchUpdate")
    public ResultInfo batchUpdate(String modelBorrowListJson) {
        if (Util.isBlank(modelBorrowListJson)) {
            return ResultInfo.buildFail("缺少必要参数");
        }
        List<ModelBorrowRecord> recordList = JSON.parseArray(modelBorrowListJson, ModelBorrowRecord.class);
        Integer modelAllId = null;
        for (ModelBorrowRecord record : recordList) {
            modelBorrowRecordService.update(record, false);
            if (modelAllId == null) {
                modelAllId = record.getModelAllId();
            }
        }
        if (modelAllId != null) {
            modelAllService.refreshLocation(modelAllId);
        }
        return ResultInfo.build();
    }

    @RequestMapping("/delete")
    public ResultInfo delete(Integer id) {
        if (Util.isBlank(id)) return ResultInfo.buildFail("缺少必要参数");
        ModelBorrowRecord record = modelBorrowRecordService.get(id);
        Integer modelAllId = record.getModelAllId();
        modelBorrowRecordService.delete(id);
        modelAllService.refreshLocation(modelAllId);
        return ResultInfo.build();
    }
    private ResultInfo checkAdd(Integer modelAllId, String borrowCustomer, String borrowCompany, String approvalPerson, String borrowTime) {
        if (Util.isBlank(borrowCustomer)) {
            return ResultInfo.buildFail("借出给谁不能为空");
        }
        if (modelAllId == null) {
            return ResultInfo.buildFail("缺少参数");
        }
        if (Util.isBlank(borrowCompany)) {
            return ResultInfo.buildFail("借出公司不能为空");
        }
        if (Util.isBlank(approvalPerson)) {
            return ResultInfo.buildFail("负责人不能为空");
        }
        if (Util.isBlank(borrowTime)) {
            return ResultInfo.buildFail("借出时间不能为空");
        }
        ResultInfo ri = modelAllService.getLastBorrowOrUpdown(modelAllId);
        JSONObject data = (JSONObject) ri.getData();
        int lasttype = data.getIntValue("lasttype");
        if (lasttype != 0 && lasttype != 2) {
            ModelBorrowRecord borrow = data.getObject("borrow", ModelBorrowRecord.class);
            ModelBorrowRecord returned = data.getObject("returned", ModelBorrowRecord.class);
            ModelUpdownRecord updown = data.getObject("updown", ModelUpdownRecord.class);
            if (lasttype == 1) {
                if (borrow.getIsReturn() != 1)
                    return ResultInfo.buildFail("借出中，不能添加借出记录！");
            } else if (lasttype == 3) {
                if (updown.getType() == 1)
                    return ResultInfo.buildFail("上场中，不能添加借出记录！");
            }
        }
        return ResultInfo.build();
    }
}
