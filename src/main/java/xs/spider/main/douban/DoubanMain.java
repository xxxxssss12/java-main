package xs.spider.main.douban;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.util.DateUtil;
import xs.spider.base.util.ExceptionWrite;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.http.HttpClientManager;
import xs.spider.base.util.http.HttpClientUtil;
import xs.spider.base.util.http.HttpReqBean;
import xs.spider.base.util.http.HttpRespBean;
import xs.spider.main.Start;
import xs.spider.work.bean.TitleInfo;
import xs.spider.work.dao.TitleInfoDao;
import xs.spider.work.service.DoubanService;

import java.util.Random;

/**
 * Created by xs on 2017/9/30.
 */
@Component
public class DoubanMain {
    private static Logger log = LogUtil.getLogger(DoubanMain.class);
    @Autowired
    private DoubanService doubanService;
    @Autowired
    private TitleInfoDao titleInfoDao;

    public void gogogo() {
        BasicCookieStore cookieStore = new BasicCookieStore();
        try(CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build()) {
            HttpClientManager.setHttpClient(httpclient);
            DoubanHttpUtil.dologin(httpclient);
            for (int i=0; i<1000; i++) {
                ResultInfo info = getFangzuInfo(httpclient, cookieStore, i);
                if (info.getCode() == -2) return;

            }
        } catch(Exception e) {
            LogUtil.error(Start.class, e, "系统发生异常");
        }
    }
    /**
     * 获取租房信息
     * @param httpclient
     * @param cookieStore
     */
    private ResultInfo getFangzuInfo(CloseableHttpClient httpclient, BasicCookieStore cookieStore, Integer pagenum) throws InterruptedException {
        Random ran = new Random();
        Thread.sleep(1000 + (ran.nextInt(3000)-1000));
        ResultInfo ri = getTopics(httpclient, cookieStore, pagenum);
        if (ri != null && ri.getCode() > 0) {
            Elements trs = (Elements) ri.getData();
            for (int i=0; i<trs.size(); i++) {
                Element row = trs.get(i);
                int titleId = doubanService.saveTitle(row, pagenum);
                if (titleId > 0) {
                    try {
                        doubanService.saveContentAndPic(titleId);
                    } catch (Exception e) {
                        LogUtil.error(getClass(),
                                "titleId=" + titleId + ";error:" + ExceptionWrite.get(e));
                    }
                }
            }
            return new ResultInfo(1, "success");
        } else {
            return ri;
        }
    }


    private ResultInfo getTopics(CloseableHttpClient httpclient, BasicCookieStore cookieStore, Integer pagenum) {
        String url = "https://www.douban.com/group/?start=" + pagenum*50;
        HttpReqBean req = new HttpReqBean(url, null, 0, null, null);
        HttpRespBean resp = HttpClientUtil.doGet(req, httpclient, cookieStore);
        if (resp.getCode() == 1) {
            try {
                Document page = Jsoup.parse(resp.getResponseBody());
                Elements trs = page.select(".topics").first().child(0).child(0).children();
                if (trs == null || trs.isEmpty()) {
                    LogUtil.info(Start.class, "没有数据了");
                    return new ResultInfo(-2, "没有数据了");
                }
                return new ResultInfo(1, "success", trs);
            } catch (Exception e) {
                LogUtil.error(Start.class, e, "爬取异常！");
            }
        }
        return new ResultInfo(-1, "爬取失败！");
    }
}
