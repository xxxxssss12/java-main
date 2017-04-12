package xs.spider.main;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.init.Log4jInit;
import xs.spider.base.util.ApplicationContextHandle;
import xs.spider.base.util.DateUtil;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.Util;
import xs.spider.base.util.http.HttpClientUtil;
import xs.spider.base.util.http.HttpReqBean;
import xs.spider.base.util.http.HttpRespBean;
import xs.spider.work.bean.TitleInfo;
import xs.spider.work.dao.TitleInfoDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xs on 2017/4/1.
 */
public class Start {
    private static Logger logger = LoggerFactory.getLogger(Start.class);
    private static ClassPathXmlApplicationContext context;
    private static TitleInfoDao titleInfoDao;
    public static void main(String[] args) throws Exception {
        logger.info("SpringContextContainer begin starting.....");
        Log4jInit.init();
        context = new ClassPathXmlApplicationContext(
                new String[]{
                        "classpath:config/applicationContext*.xml"
                }
        );
        context.start();
        logger.info("SpringContextContainer is starting.....");
        titleInfoDao = (TitleInfoDao) ApplicationContextHandle.getBean("titleInfoDao");
        //--------------------------------------------开始----------------------------------------
        BasicCookieStore cookieStore = new BasicCookieStore();
        try(CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build()) {
            ResultInfo ri = HttpClientUtil.doGet("https://www.douban.com/login?source=group", null);
            Map<String, Object> map = new HashMap<>();
            map.put("source", "group");
            map.put("redir", "https://www.douban.com/group/");
            map.put("form_email", "xs94xs@sina.com");
            map.put("form_password", "wdmmwrz123");
            map.put("remember", "on");
            List<NameValuePair> pairList = new ArrayList<>();
            if (ri.getCode() == 1) {
                Document loginPage = Jsoup.parse(Util.null2string(ri.getData()));
                Elements elements = loginPage.select("input[name=captcha-id]");
                if (elements != null && !Util.isBlank(elements.first().val())) {
                    String captchaId = elements.first().val();
                    String yzm_pic_url = loginPage.select("[id=captcha_image]").attr("src");
                    HttpClientUtil.getStaticToFile(yzm_pic_url, "C:\\Users\\hasee\\Desktop\\a.jpg");
                    String yzm = Util.getLineOnKeyBoradInput();
                    pairList.add(new BasicNameValuePair("captcha-id", captchaId));
                    pairList.add(new BasicNameValuePair("captcha-solution", yzm));
                    map.put("captcha-id", captchaId);
                    map.put("captcha-solution", yzm);
                } else {
                    LogUtil.info(Start.class, "不需要验证码...");
                }
                LogUtil.info(HttpClientUtil.class, "登录开始。。。");
                HttpReqBean reqBean = new HttpReqBean("https://accounts.douban.com/login", map, 1, null, null);
                HttpRespBean resp = HttpClientUtil.doPost(reqBean, httpclient, cookieStore);
                if (resp.getCode() != 1) {
                    LogUtil.info(Start.class, resp.getMessage());
                    return;
                }
                LogUtil.info(HttpClientUtil.class, "登录完毕。。。");
                for (int i=0; i<2000; i++) {
                    getFangzuInfo(httpclient, cookieStore, i);
                }
            } else {
                System.out.println(ri.getMessage());
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
                    titleInfo.setTime(DateUtil.parseStringToDate(row.select(".td-time").first().attr("title")
                            , DateUtil.C_YYYY_MM_DD_HH_MM_SS));
                    try {
                        titleInfoDao.save(titleInfo);
                    } catch (Exception e) {
                        LogUtil.info(Start.class, "重复信息");
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
