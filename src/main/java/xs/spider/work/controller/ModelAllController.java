package xs.spider.work.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.spider.base.bean.PageBean;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.util.DateUtil;
import xs.spider.base.util.TypeConvUtil;
import xs.spider.base.util.Util;
import xs.spider.work.bean.ModelAll;
import xs.spider.work.bean.ModelDetail;
import xs.spider.work.bean.ModelType;
import xs.spider.work.service.ModelAllServiceImpl;
import xs.spider.work.service.ModelDetailServiceImpl;
import xs.spider.work.service.ModelTypeServiceImpl;
import xs.spider.work.shiro.CurrentUserHelper;

import java.util.Date;
import java.util.List;

/**
 * Created by xs on 2017/10/6.
 */
@RestController
@RequestMapping("/modelAll")
public class ModelAllController {
    @Autowired
    private ModelAllServiceImpl modelAllService;
    @Autowired
    private ModelTypeServiceImpl modelTypeService;
    @Autowired
    private ModelDetailServiceImpl modelDetailService;
    @RequestMapping("/getByPage")
    public JSONObject getByPage(String modelNo, Integer modelTypeId, String remark, String storeLocation) {
        PageBean<ModelAll> page = modelAllService.getPage(modelNo, modelTypeId, remark, storeLocation);
        return TypeConvUtil.convertPageToLiger(page);
    }

    @RequestMapping("/getAllType")
    public ResultInfo getAllType() {
        List<ModelType> list = modelTypeService.getAll();
        if (list == null || list.isEmpty()) return ResultInfo.buildFail();
        return ResultInfo.build(list);
    }

    @RequestMapping("/batchUpdate")
    public ResultInfo batchUpdate(String modelAllListJson) {

        if (Util.isBlank(modelAllListJson)) {
            return ResultInfo.buildFail("缺少必要参数");
        }
        List<ModelAll> modelAllList = JSON.parseArray(modelAllListJson, ModelAll.class);
        for (ModelAll modelAll : modelAllList) {
            modelAllService.update(modelAll, false);
        }
        return ResultInfo.build();
    }

    @RequestMapping("/delete")
    public ResultInfo delete(Integer id) {
        if (Util.isBlank(id)) return ResultInfo.buildFail("缺少必要参数");
        ModelAll modelAll = new ModelAll();
        modelAll.setId( id);
        modelAll.setIsDel(1);
        modelAllService.update(modelAll, false);
        return ResultInfo.build();
    }

    @RequestMapping("/add")
    public ResultInfo add(
            String modelNo,
            String name,
            String lingjiancaizhi,
            String createTime,
            String createLocation,
            String storeLocation,
            Integer functionTypeId,
            Integer useLevelId,
            String materialQuality,
            Integer modelTypeId,
            String remark,
            String detail) {
        if (Util.isBlank(modelNo)) {
            return ResultInfo.buildFail("图号不能为空！");
        }
        ModelAll modelAll = new ModelAll();
        modelAll.setModelNo(modelNo);
        modelAll.setName(name);
        modelAll.setLingjiancaizhi(lingjiancaizhi);
        modelAll.setCreateTime(Util.isBlank(createTime) ? null : DateUtil.parseStringToDate(createTime, DateUtil.C_YYYY_MM_DD));
        modelAll.setCreateLocation(createLocation);
        modelAll.setStoreLocation(storeLocation);
        modelAll.setFunctionTypeId(functionTypeId);
        modelAll.setUseLevelId(useLevelId);
        modelAll.setMaterialQuality(materialQuality);
        modelAll.setModelTypeId(modelTypeId);
        modelAll.setRemark(remark);
        modelAll.setAddtime(new Date());
        modelAll.setCreateUser(CurrentUserHelper.getCurrentUsername());
        Integer id = modelAllService.save(modelAll);
        if (!Util.isBlank(detail)) {
            String[] detailArr = detail.split("\\|");
            for (String d : detailArr) {
                if (!Util.isBlank(d)) {
                    ModelDetail modelDetail = new ModelDetail();
                    modelDetail.setModelAllId(id);
                    modelDetail.setDetail(d);
                    modelDetailService.save(modelDetail);
                }
            }
        }
        return ResultInfo.build(id);
    }
}
