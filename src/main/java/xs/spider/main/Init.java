package xs.spider.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import xs.spider.base.init.Log4jInit;

/**
 * Created by xs on 2017/4/13.
 */
public class Init {
    private static Logger logger = LoggerFactory.getLogger(Init.class);
    private static ClassPathXmlApplicationContext context;
    public static void init() {

        logger.info("SpringContextContainer begin starting.....");
        Log4jInit.init();
        context = new ClassPathXmlApplicationContext(
                new String[]{
                        "classpath:config/applicationContext.xml"
                }
        );
        context.start();
        logger.info("SpringContextContainer is starting.....");
    }
}
