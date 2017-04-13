package xs.spider.work.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.spider.base.bean.PageBean;
import xs.spider.work.dao.TitleInfoDao;

import javax.annotation.Resource;

/**
 * Created by xs on 2017/4/12.
 */
@RestController
@RequestMapping("/title")
public class TitleController {
    @Resource
    private TitleInfoDao titleInfoDao;
    @RequestMapping("/getByPage")
    public PageBean getByPage(String title) throws Exception {
        return titleInfoDao.getPage(title);
    }
}
