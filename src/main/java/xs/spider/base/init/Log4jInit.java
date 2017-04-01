package xs.spider.base.init;

import org.apache.log4j.PropertyConfigurator;
import xs.spider.base.config.ConfigProvider;

/**
 * Created by xs on 2017/4/1.
 */
public class Log4jInit {
    public static void init() {
        PropertyConfigurator.configure(ConfigProvider.getProp());//装入log4j配置信息
    }
}
