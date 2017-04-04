package xs.spider.main;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.init.Log4jInit;
import xs.spider.base.util.ExceptionWrite;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.Util;
import xs.spider.base.util.http.HttpClientUtil;
import xs.spider.base.util.http.HttpReqBean;
import xs.spider.base.util.http.HttpRespBean;

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
                getFangzuInfo(httpclient, cookieStore);
            } else {
                System.out.println(ri.getMessage());
            }
        } catch(Exception e) {
            LogUtil.error(Start.class, e, "系统发生异常");
        }
    }

    private static void getFangzuInfo(CloseableHttpClient httpclient, BasicCookieStore cookieStore) {
    }
}
