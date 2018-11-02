package xs.spider.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import xs.spider.base.init.Log4jInit;
import xs.spider.base.util.LogUtil;
import xs.spider.work.service.CBServiceImpl;

import java.util.List;
import java.util.Map;

/**
 * Created by xs on 2017/4/1.
 */
public class Start {
    private static Logger logger = LogUtil.getLogger(Start.class);
    private static ClassPathXmlApplicationContext context;
    private static void init() {
        logger.info("SpringContextContainer begin starting.....");
        Log4jInit.init();
        context = new ClassPathXmlApplicationContext(
                new String[]{
                        "classpath*:config/applicationContext*"
                }
        );
        context.start();
        logger.info("SpringContextContainer is starting.....");
    }
    public static void main(String[] args) {
        for (int i=0; i<20; i++) {
            for (int j=0;j<=i;j++) {
                if ((j % 2 ^ i % 2) == 0) {
                    System.out.printf("*");
                } else System.out.printf(" ");
            }
            System.out.println();
        }
//        init();
//        AutoTest.doAutoTest();
//        testImport();
    }

    private static void testImport() {
        CBServiceImpl cbService = context.getBean(CBServiceImpl.class);
        cbService.importOnXls("/Users/xs/Documents/xs/长沙水泵厂/import.xls");
    }
}
