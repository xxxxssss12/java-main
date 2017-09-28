package xs.spider.work.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.util.ExceptionWrite;
import xs.spider.base.util.Util;
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
    public ResultInfo get(String phone, Integer id) throws Exception {
        if (id == null) {
            return customerService.getInfo(phone, id);
        } else {
            return customerService.getInfo(null, id);
        }
    }
    @RequestMapping("/check")
    public ResultInfo check(String customerIdStr) throws Exception {
        String[] customerIds = customerIdStr.split(",");
        StringBuilder sb = new StringBuilder();
        for (String custId : customerIds) {
            Integer id = -1;
            try {
                if (!Util.isBlank(custId))
                    id = Integer.parseInt(custId.trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (id > 0) {
                sb.append("customerId=").append(id).append("...")
                        .append(customerService.checkLevelVipQuestion(null, id));
                sb.append("<br>");
            }
        }
        return new ResultInfo(1, "success", sb.toString());
    }
}
