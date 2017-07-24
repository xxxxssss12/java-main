package xs.spider.main;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import xs.spider.base.bean.ResultInfo;
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
 * Created by xs on 2017/4/14.
 */
public class DoubanHttpUtil {
    public static void dologin(CloseableHttpClient httpClient) throws Exception {
        ResultInfo ri = HttpClientUtil.doGet("https://www.douban.com/login?source=group", null);
        Map<String, Object> map = new HashMap<>();
        map.put("source", "group");
        map.put("redir", "https://www.douban.com/group/");
        map.put("form_email", "xs94xs@sina.com");
        map.put("form_password", "");
        map.put("remember", "on");
        List<NameValuePair> pairList = new ArrayList<>();
        if (ri.getCode() == 1) {
            Document loginPage = Jsoup.parse(Util.null2string(ri.getData()));
            Elements elements = loginPage.select("input[name=captcha-id]");
            if (elements != null && !Util.isBlank(elements.first())) {
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
            LogUtil.info(DoubanHttpUtil.class, "登录开始。。。");
            HttpReqBean reqBean = new HttpReqBean("https://accounts.douban.com/login", map, 1, null, null);
            HttpRespBean resp = HttpClientUtil.doPost(reqBean, httpClient, null);
            if (resp.getCode() != 1) {
                LogUtil.info(Start.class, resp.getMessage());
                return;
            }
            LogUtil.info(DoubanHttpUtil.class, "登录完毕。。。");
        } else {
            LogUtil.info(DoubanHttpUtil.class, ri.getMessage());
        }
    }
}
