package xs.spider.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.init.Log4jInit;
import xs.spider.work.service.CustomerServiceImpl;

import java.util.Arrays;
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
                        "classpath*:config/applicationContext*"
                }
        );
        context.start();
        logger.info("SpringContextContainer is starting.....");
        dealUpLevelInfo();
    }

    private static void dealUpLevelInfo() throws Exception {
        CustomerServiceImpl customerService = context.getBean(CustomerServiceImpl.class);
        List<Integer> custIds = Arrays.asList(
                2822487,
                23833992,
                23797323,
                23680285,
                23681235,
                23619951,
                23496421,
                23538897,
                22937341,
                22848171,
                22223121,
                20638901,
                20376328,
                19555243,
                19099118,
                18417790,
                16316705,
                16136785,
                15131094,
                13612865,
                10533097,
                12973936,
                2267266,
                40684,
                5307353,
                2736007,
                10741896);
        for (Integer a : custIds) {
            System.out.println("检查数据..." + a + "..." + customerService.checkLevelVipQuestion(null, a));
        }
    }
}
