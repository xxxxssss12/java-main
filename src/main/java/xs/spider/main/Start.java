package xs.spider.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import xs.spider.base.init.Log4jInit;
import xs.spider.work.service.CBServiceImpl;

/**
 * Created by xs on 2017/4/1.
 */
public class Start {
    private static Logger logger = LoggerFactory.getLogger(Start.class);
    private static ClassPathXmlApplicationContext context;
    public static void main(String[] args) {
        logger.info("SpringContextContainer begin starting.....");
        Log4jInit.init();
        context = new ClassPathXmlApplicationContext(
                new String[]{
                        "classpath*:config/applicationContext*"
                }
        );
        context.start();
        logger.info("SpringContextContainer is starting.....");
        testImport();
    }

    private static void testImport() {
        CBServiceImpl cbService = context.getBean(CBServiceImpl.class);
        cbService.importOnXls("D:\\documents\\!xs\\长沙水泵厂\\顺模具专用\\标准件8b.xls");
    }
}
