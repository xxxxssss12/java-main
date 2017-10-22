package xs.spider.work.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.spider.base.bean.ResultInfo;
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
    public ResultInfo getByModelId(Integer modelId) {
        if (modelId == null) return ResultInfo.buildFail("缺少必要参数");
        ModelDetail modelDetail = new ModelDetail();
        modelDetail.setModelAllId(modelId);
        List<ModelDetail> modelDetailList = modelDetailService.getList(modelDetail);
        return ResultInfo.build(modelDetailList);
    }
}
