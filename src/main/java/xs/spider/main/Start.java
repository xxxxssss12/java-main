package xs.spider.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import xs.spider.base.init.Log4jInit;
import xs.spider.main.douban.DoubanMain;
import xs.spider.work.service.DoubanService;

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
                new String[] {
                        "classpath*:config/applicationContext*"
                }
        );
        context.start();
        logger.info("SpringContextContainer is starting.....");
        importAllToEs();
//        DoubanMain doubanMain = context.getBean(DoubanMain.class);
//        doubanMain.gogogo();
//        SubwayService subwayService = context.getBean(SubwayService.class);
//        subwayService.importStationAndLineInfo();
//        BasicCookieStore cookieStore = new BasicCookieStore();
//        try(CloseableHttpClient httpclient = HttpClients.custom()
//                .setDefaultCookieStore(cookieStore)
//                .build()) {
//            HttpClientManager.setHttpClient(httpclient);
//            DoubanHttpUtil.dologin(httpclient);
//            testSavePicAndContent();
//        } catch(Exception e) {
//            LogUtil.error(Start.class, e, "系统发生异常");
//        }
    }

    private static void importAllToEs() {

        DoubanService doubanService = context.getBean(DoubanService.class);
        doubanService.importAllToEs();
    }

    private static void testSavePicAndContent() {
        DoubanService doubanService = context.getBean(DoubanService.class);
        doubanService.saveContentAndPic(190762);
    }
}
