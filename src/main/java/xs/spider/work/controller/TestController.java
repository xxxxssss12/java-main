package xs.spider.work.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.spider.base.bean.ResultInfo;
import xs.spider.work.bean.TestBean;
import xs.spider.work.service.TestServiceImpl;

import java.util.Date;

/**
 * Created by xs on 2017/7/29
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private TestServiceImpl testService;
    @RequestMapping("/insert")
    public ResultInfo insert(String testc) {
        TestBean bean = new TestBean();
        bean.setUpdateTime(new Date());
        bean.setTestC(testc);
        bean.setCreateTime(new Date());
        testService.save(bean);
        return new ResultInfo(1, "success", bean);
    }

    /**
     * draw=pagenum
     * length=pagesize
     * @param pagenum
     * @param pagesize
     * @return
     */
    @RequestMapping("/getByPage")
    public ResultInfo getByPage(Integer pagenum, Integer pagesize) {
        if (pagenum == null) pagenum = 1;
        if (pagesize == null) pagesize = 20;
        TestBean bean = new TestBean();
        return ResultInfo.buildSucc(testService.getPage(bean, "id desc", pagenum, pagesize));
    }
}
