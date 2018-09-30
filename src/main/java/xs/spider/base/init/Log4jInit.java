package xs.spider.base.init;

import org.apache.log4j.PropertyConfigurator;
import xs.spider.base.config.ConfigProvider;
import xs.spider.base.util.PropUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by xs on 2017/4/1.
 */
public class Log4jInit implements ServletContextListener {
    private static String log_root_path = PropUtil.getPropValueFromClasspath("config","log.root");
    public static void init() {
        System.out.println("装配log4j");
        if(System.getProperty("log.root") == null) {
            System.setProperty("log.root", log_root_path);
        }
        PropertyConfigurator.configure(ConfigProvider.getProp());//装入log4j配置信息
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
