package xs.spider.work.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.spider.base.bean.PageBean;
import xs.spider.base.bean.ResultInfo;
import xs.spider.work.bean.TitleInfo;
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
    @RequestMapping("/remove")
    public ResultInfo remove(Integer id) throws Exception {
        if (id == null) return new ResultInfo(-1, "缺少参数");
        TitleInfo info = titleInfoDao.get(id);
        info.setIsValid(-1);
        titleInfoDao.update(info, false);
        return new ResultInfo(1,"成功");
    }
}
