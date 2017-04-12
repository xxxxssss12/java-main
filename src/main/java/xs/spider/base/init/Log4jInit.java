package xs.spider.base.init;

import org.apache.log4j.PropertyConfigurator;
import xs.spider.base.config.ConfigProvider;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.PropUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Created by xs on 2017/4/1.
 */
public class Log4jInit extends HttpServlet {
    private static String log_root_path = PropUtil.getPropValueFromClasspath("config","log.root");
    public static void init1() {
        if(System.getProperty("log.root") == null) {
            System.setProperty("log.root", log_root_path);
        }
        PropertyConfigurator.configure(ConfigProvider.getProp());//装入log4j配置信息
        LogUtil.info(Log4jInit.class,"init over...");
    }
    public void init(ServletConfig config) throws ServletException {
        init1();
    }
}
