package xs.spider.base.config;

import org.apache.commons.io.IOUtils;
import xs.spider.base.util.LogUtil;

import java.io.*;
import java.util.Properties;

/**
 * Created by xs on 2017/4/1.
 */
public class ConfigProvider {
    private static Properties prop;
    public static final String DEFAULT_CONFIG_PATH = "classpath:/config/config.properties";
    public static final String CLASSPATH_PREFIX = "classpath:";
    public static final String FILE_PREFIX = "file:";

    public static Properties getProp() {
        if (prop == null) {
            init();
        }
        return prop;
    }

    private static void init() {
        //已经初始化，则直接返回
        if (prop != null
                && prop.size() > 0) {
            return;
        }

        LogUtil.info(ConfigProvider.class, "init FundamentalConfigProvider.prop.");
        Properties properties = null;
        if (properties != null && properties.size() > 0) {
            prop = properties;
            LogUtil.info(ConfigProvider.class, "find properties from ConfigProperties");
        } else {
            String configPathStr = System.getProperty("config.path");

            if (configPathStr == null) {
                LogUtil.info(ConfigProvider.class,"can't load config from:System.getProperty(Constants.CONFIG_PATH)");
                configPathStr = System.getenv("config.path");
                LogUtil.info(ConfigProvider.class,"can't load config from:System.getenv(Constants.CONFIG_PATH)");
                if (configPathStr == null) {
                    LogUtil.info(ConfigProvider.class,"config.path is null，now we use default config.if the environment is not dev,please check you startup param: -Dconfig.path=xxx");
                    configPathStr = DEFAULT_CONFIG_PATH;
                }
            }
            LogUtil.info(ConfigProvider.class,"config.path:"+ configPathStr);

            prop = new Properties();

            String[] configPathArray = configPathStr.split(";");
            for(String configPath:configPathArray){
                doInit(configPath);
            }

        }

        LogUtil.info(ConfigProvider.class,"FundamentalConfigProvider.size:"+prop.size());
    }

    private static void doInit(String configPath) {
        FileInputStream fileInputStream = null;
        InputStream inputStream = null;
        try {
            if (configPath.startsWith(FILE_PREFIX)) {
                iniWithDir(new File(configPath.replace(FILE_PREFIX,"")));
            } else if (configPath.startsWith(CLASSPATH_PREFIX)) {
                configPath = configPath
                        .replace(CLASSPATH_PREFIX, "");
                inputStream = ConfigProvider.class
                        .getResourceAsStream(configPath);
                prop.load(inputStream);
            }else{
                iniWithDir(new File(configPath));
            }
        } catch (FileNotFoundException e) {
            LogUtil.error(ConfigProvider.class, e);
        } catch (IOException e) {
            LogUtil.error(ConfigProvider.class, e);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(inputStream);
        }
    }

    private static void iniWithDir(File file) {
        if(file==null) return;
        if(file.isDirectory()){
            for(File f:file.listFiles()){
                iniWithDir(f);
            }
        }else if (file.isFile()&&file.getName().endsWith(".properties")){
            try {
                prop.load(new FileInputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 返回String类型的配置结果
     *
     * @param key
     * @return
     */
    public static String get(String key) {
        return getProp().getProperty(key);
    }

    /**
     * 返回String类型的配置结果
     * @param key
     * @param defaultValue 如果值为空, 返回defaultValue
     * @return
     */
    public static String get(String key,String defaultValue) {
        String prop = getProp().getProperty(key);
        if(prop==null){
            return defaultValue;
        }
        return prop;
    }

    /**
     * 返回Integer型的配置结果。如果没有找到配置，返回null
     *
     * @param key
     * @return
     */
    public static Integer getInt(String key) {
        String value = get(key);
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            LogUtil.error(ConfigProvider.class, ex,"get int error, return null.");
            return null;
        }
    }

    /**
     * 返回Boolean型的配置结果。如果没有找到配置，返回false
     *
     * @param key
     * @return
     */
    public static boolean getBoolean(String key) {
        String value = get(key);
        return Boolean.valueOf(value);
    }

    /**
     * 返回Double型的配置结果。如果没有找到配置，返回null
     *
     * @param key
     * @return
     */
    public static Double getDouble(String key) {
        String value = get(key);
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            LogUtil.error(ConfigProvider.class, e,"get int error, return null.");
            return null;
        }
    }

    /**
     * 返回Long型的配置结果。如果没有找到配置，返回null
     *
     * @param key
     * @return
     */
    public static Long getLong(String key) {
        String value = get(key);
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            LogUtil.error(ConfigProvider.class, e,"get int error, return null.");
            return null;
        }
    }
}
