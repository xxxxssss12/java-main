package xs.spider.work.service;

import org.springframework.stereotype.Service;
import xs.spider.base.dao.DaoSupport;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.Util;
import xs.spider.work.bean.AccessStatistic;
import xs.spider.work.shiro.CurrentUserHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class AccessStatisticServiceImpl extends DaoSupportImpl<AccessStatistic, Long> implements DaoSupport<AccessStatistic, Long> {

    public void save(HttpServletRequest request) {
        try {
            String username = CurrentUserHelper.getCurrentUsername();
            String host = request.getLocalName();
            if (Util.isBlank(host)) {
                host = request.getLocalAddr();
            }
            String clientIp = Util.getIpAddr(request);
            String reqUrl = request.getRequestURI();
            AccessStatistic statistic = new AccessStatistic();
            statistic.setClientIp(clientIp);
            statistic.setHost(host);
            statistic.setProjectName("changShaShuiBengChang");
            statistic.setReqUrl(reqUrl);
            statistic.setUsername(username);
            statistic.setAccessTime(new Date());
            this.save(statistic);
        } catch (Exception e) {
            LogUtil.error(this.getClass(), e, "系统发生异常");
        }
    }
}
