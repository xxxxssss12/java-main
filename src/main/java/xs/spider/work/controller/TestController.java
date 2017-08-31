package xs.spider.work.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.spider.base.bean.ResultInfo;
import xs.spider.work.bean.TestBean;
import xs.spider.work.service.CustomerServiceImpl;
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
    @Autowired
    private CustomerServiceImpl customerService;
    @RequestMapping("/get")
    public ResultInfo get(String phone) throws Exception {
        return customerService.getInfo(phone);
    }
}
