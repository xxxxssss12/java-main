package xs.spider.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.init.Log4jInit;
import xs.spider.work.service.CustomerServiceImpl;

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
                        "classpath*:config/applicationContext*"
                }
        );
        context.start();
        logger.info("SpringContextContainer is starting.....");
        CustomerServiceImpl service = context.getBean(CustomerServiceImpl.class);
        ResultInfo info = service.getInfo("13872923990");
        System.out.println(JSON.toJSONString(info));
    }
}
