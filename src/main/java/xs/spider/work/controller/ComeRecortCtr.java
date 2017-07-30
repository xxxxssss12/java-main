package xs.spider.work.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.util.Util;
import xs.spider.work.bean.ComeRecord;
import xs.spider.work.service.ComeRecordService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by xs on 2017/7/30
 */
@RestController
@RequestMapping("/come")
public class ComeRecortCtr {
    @Autowired
    private ComeRecordService comeRecordService;
    @RequestMapping("/insert")
    public ResultInfo insert(HttpServletRequest request) {
        String ip = Util.getIpAddress(request);
        ComeRecord record = new ComeRecord();
        record.setCreateTime(new Date());
        record.setLastUpTime(new Date());
        record.setIp(ip);
        record.setStayTime(0l);
        Integer id = comeRecordService.save(record);
        return new ResultInfo(1, "success", id);
    }
    @RequestMapping("/update")
    public ResultInfo update(Integer id, Integer failTime) {
        if (id == null) return new ResultInfo(-1, "param lost");
        ComeRecord record = comeRecordService.get(id);
        record.setFailTime(failTime);
        record.setStayTime(new Date().getTime() - record.getCreateTime().getTime());
        comeRecordService.update(record, false);
        return new ResultInfo(1, "success");
    }
}
