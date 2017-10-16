package xs.spider.work.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.spider.base.bean.PageBean;
import xs.spider.base.util.BeanUtil;
import xs.spider.work.bean.ModelAll;
import xs.spider.work.service.ModelAllServiceImpl;

/**
 * Created by xs on 2017/10/6.
 */
@RestController
@RequestMapping("/modelAll")
public class ModelAllController {
    @Autowired
    private ModelAllServiceImpl modelAllService;
    @RequestMapping("/getByPage")
    public PageBean getByPage() {
        PageBean<ModelAll> page = modelAllService.getPage("select * from " + BeanUtil.getTableName(ModelAll.class), null);
        return page;
    }
}
