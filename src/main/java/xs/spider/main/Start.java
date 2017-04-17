package xs.spider.main;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xs.spider.base.util.ApplicationContextHandle;
import xs.spider.base.util.DateUtil;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.http.HttpClientUtil;
import xs.spider.base.util.http.HttpReqBean;
import xs.spider.base.util.http.HttpRespBean;
import xs.spider.work.bean.TitleInfo;
import xs.spider.work.dao.TitleInfoDao;

/**
 * Created by xs on 2017/4/1.
 */
public class Start {
    static {
        Init.init();
    }
    private static TitleInfoDao titleInfoDao = (TitleInfoDao) ApplicationContextHandle.getBean("titleInfoDao");
    private static Logger log = LogUtil.getLogger(Start.class);
    public static void main(String[] args) throws Exception {
        //--------------------------------------------开始----------------------------------------
        BasicCookieStore cookieStore = new BasicCookieStore();
        try(CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build()) {
            DoubanHttpUtil.dologin(httpclient);
            for (int i=0; i<1000; i++) {
                getFangzuInfo(httpclient, cookieStore, i);
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
    private static void getFangzuInfo(CloseableHttpClient httpclient, BasicCookieStore cookieStore, Integer pagenum) {
        String url = "https://www.douban.com/group/?start=" + pagenum*50;
        HttpReqBean req = new HttpReqBean(url, null, 0, null, null);
        HttpRespBean resp = HttpClientUtil.doGet(req, httpclient, cookieStore);
        if (resp.getCode() == 1) {
            try {
                Document page = Jsoup.parse(resp.getResponseBody());
                Elements trs = page.select(".topics").first().child(0).child(0).children();
                if (trs.isEmpty()) {
                    LogUtil.info(Start.class, "没有数据了");
                    System.exit(0);
                }
                for (int i=0; i<trs.size(); i++) {
                    Element row = trs.get(i);
                    TitleInfo titleInfo = new TitleInfo();
                    titleInfo.setContent(row.child(0).select("a").first().attr("title"));
                    titleInfo.setUrl(row.child(0).select("a").first().attr("href"));
                    titleInfo.setPagenum(pagenum+1);
                    titleInfo.setTime(DateUtil.parseStringToDate(row.select(".td-time").first().attr("title"),
                            DateUtil.C_YYYY_MM_DD_HH_MM_SS));
                    try {
                        titleInfoDao.save(titleInfo);
                    } catch (Exception e) {
                        log.info("房源已存在");
                    }
                }
            } catch (Exception e) {
                LogUtil.error(Start.class, e, "爬取异常！");
            }
        } else {
            LogUtil.info(Start.class, "请求异常");
        }
    }
}
