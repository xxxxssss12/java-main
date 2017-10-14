package xs.spider.work.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.spider.base.bean.PageBean;

/**
 * Created by xs on 2017/10/6.
 */
@RestController
@RequestMapping("/modelAll")
public class ModelAllController {
    @RequestMapping("/getByPage")
    public PageBean getByPage() {

        return null;
    }
}
