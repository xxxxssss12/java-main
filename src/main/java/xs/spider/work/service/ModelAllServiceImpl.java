package xs.spider.work.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import xs.spider.base.bean.PageBean;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.dao.DaoSupport;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.base.util.Util;
import xs.spider.work.bean.ModelAll;
import xs.spider.work.bean.ModelBorrowRecord;
import xs.spider.work.bean.ModelUpdownRecord;
import xs.spider.work.dao.ModelAllDao;
import xs.spider.work.dao.ModelBorrowRecordDao;
import xs.spider.work.dao.ModelUpdownRecordDao;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xs on 2017/8/9.
 */
@Service
public class ModelAllServiceImpl extends DaoSupportImpl<ModelAll, Integer> implements DaoSupport<ModelAll, Integer> {
    @Resource
    private ModelBorrowRecordDao modelBorrowRecordDao;
    @Resource
    private ModelUpdownRecordDao modelUpdownRecordDao;
    @Resource
    private ModelAllDao modelAllDao;
    public PageBean<ModelAll> getPage(String modelNo, Integer modelTypeId, String remark, String storeLocation) {
        StringBuilder sql = new StringBuilder("SELECT t.* FROM tb_model_all t WHERE isDel=0 ");
        List<Object> list = new ArrayList<>();
        if (!Util.isBlank(modelNo)) {
            sql.append(" AND modelNo like '%").append(modelNo).append("%'");
        }
        if (!Util.isBlank(modelTypeId)) {
            sql.append(" AND modelTypeId=? ");
            list.add(modelTypeId);
        }
        if (!Util.isBlank(remark)) {
            sql.append(" AND remark like '%").append(remark).append("%'");
        }
        if (!Util.isBlank(storeLocation)) {
            sql.append(" AND storeLocation like '%").append(storeLocation).append("%'");
        }
        return getPage(sql.toString(), list);
    }

    public void refreshLocation(Integer modelId) {
        ResultInfo ri = getLastBorrowOrUpdown(modelId);
        JSONObject data = (JSONObject) ri.getData();
        doRefresh(modelId,
                data.getObject("borrow", ModelBorrowRecord.class),
                data.getObject("returned", ModelBorrowRecord.class),
                data.getObject("updown", ModelUpdownRecord.class),
                data.getIntValue("lasttype"));
    }
    public ResultInfo getLastBorrowOrUpdown(Integer modelId) {
        JSONObject data = new JSONObject();
        ModelBorrowRecord borrow = new ModelBorrowRecord();
        ModelBorrowRecord returned;
        ModelUpdownRecord updown = new ModelUpdownRecord();

        borrow.setModelAllId(modelId);
        updown.setModelAllId(modelId);
        List<ModelBorrowRecord> borrowList = modelBorrowRecordDao.getList(borrow, " borrowTime desc ");
        List<ModelBorrowRecord> returnList = modelBorrowRecordDao.getList(borrow, " returnTime desc ");
        List<ModelUpdownRecord> updownList = modelUpdownRecordDao.getList(updown, " actionTime desc ");
        int lasttype = 0;
        Date lastTime = null;
        borrow = null;
        returned = null;
        updown = null;
        if (borrowList != null && !borrowList.isEmpty()) borrow = borrowList.get(0);
        if (returnList != null && !returnList.isEmpty()) returned = returnList.get(0);
        if (updownList != null && !updownList.isEmpty()) updown = updownList.get(0);
        if (borrow != null && borrow.getBorrowTime() != null) {
            lasttype = 1;
            lastTime = borrow.getBorrowTime();
        }
        if (returned != null && returned.getReturnTime() != null && (lastTime == null || lastTime.getTime() < returned.getReturnTime().getTime())) {
            lasttype = 2;
            lastTime = returned.getReturnTime();
        }
        if (updown != null && updown.getActionTime() != null && (lastTime == null || lastTime.getTime() < updown.getActionTime().getTime())) {
            lasttype = 3;
        }
        data.put("lasttype", lasttype);
        data.put("borrow", borrow);
        data.put("returned", returned);
        data.put("updown", updown);
        return ResultInfo.build(data);
    }
    private void doRefresh(Integer modelId, ModelBorrowRecord borrow, ModelBorrowRecord returned, ModelUpdownRecord updown, int lasttype) {
        ModelAll modelAll = new ModelAll();
        modelAll.setId(modelId);
        switch (lasttype) {
            case 1:
                if (borrow.getIsReturn() != 1) {
                    modelAll.setStoreLocation(borrow.getBorrowCompany());
                    modelAllDao.update(modelAll, false);
                } else if (!Util.isBlank(borrow.getPreLocation())) {
                    modelAll.setStoreLocation(borrow.getPreLocation());
                    modelAllDao.update(modelAll, false);
                }
                return;
            case 2:
                if (!Util.isBlank(returned.getPreLocation())) {
                    modelAll.setStoreLocation(returned.getPreLocation());
                    modelAllDao.update(modelAll, false);
                }
                return;
            case 3:
                if (!Util.isBlank(updown.getNowLocation())) {
                    modelAll.setStoreLocation(updown.getNowLocation());
                    modelAllDao.update(modelAll, false);
                }
                return;
            default: return;
        }
    }
}
