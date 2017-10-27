package xs.spider.work.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.spider.base.bean.PageBean;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.pager.PageContext;
import xs.spider.base.util.TypeConvUtil;
import xs.spider.work.bean.ModelDetail;
import xs.spider.work.service.ModelDetailServiceImpl;

import java.util.List;

/**
 * Created by xs on 2017/10/22
 */
@RestController
@RequestMapping("/modelDetail")
public class ModelDetailController {
    @Autowired
    private ModelDetailServiceImpl modelDetailService;

    @RequestMapping("/getByModelId")
    public JSONObject getByModelId(Integer modelId) {
        if (modelId == null) return JSON.parseObject(ResultInfo.buildFail("缺少必要参数").toString());
        ModelDetail modelDetail = new ModelDetail();
        modelDetail.setModelAllId(modelId);
        PageContext.initPage(1, 100);
        PageBean<ModelDetail> modelDetailPage = modelDetailService.getPage(modelDetail);
        return TypeConvUtil.convertPageToLiger(modelDetailPage);
    }
}
