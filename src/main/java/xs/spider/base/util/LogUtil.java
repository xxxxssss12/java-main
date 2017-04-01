package xs.spider.base.util;

import org.apache.log4j.*;

import java.io.File;
import java.io.IOException;

public class LogUtil {
	private LogUtil() {

	}

	/**
	 * 获取Log4j对象
	 * 
	 * @param clzz
	 *            用于日志文件命名，例如：core.util.LogUtil_20130625.log
	 * @param logsPath
	 *            日志文件存放路径
	 * @return
	 */
	public static Logger getLogger(Class<?> clzz, String logsPath) {
		Logger logger = null;
		logger = Logger.getLogger(clzz);
		logger.addAppender(getAppender(clzz.getName(), logsPath));
		logger.addAppender(getConsolAppender());
		logger.setAdditivity(false);
		return logger;
	}
	/**
	 * 获取Log4j对象
	 * 
	 * @param clzz
	 *            用于日志文件命名，例如：core.util.LogUtil_20130625.log
	 * @return
	 */
	public static Logger getLogger(Class<?> clzz) {
		return getLogger(clzz, "logs");
	}
	private static Appender getConsolAppender() {
		String pattern = "[%-5p] %d{yyyy-MM-dd HH:mm:ss,SSS} %c - %m%n";
		Layout layout = new PatternLayout(pattern);
		Appender appender = new ConsoleAppender(layout);
		return appender;
	}
	private static DailyRollingFileAppender getAppender(String className,
			String logsPath) {
		DailyRollingFileAppender appender = null;
		try {
			String pattern = "[%-5p] %d{yyyy-MM-dd HH:mm:ss,SSS} - %m%n";
			Layout layout = new PatternLayout(pattern);
			appender = new DailyRollingFileAppender(layout, getPath(className,
					logsPath), "'_'yyyyMMdd'.log'");
		} catch (IOException e) {
			new Exception(e);
		}
		return appender;
	}
	/**
	 * 取得log文件的路径
	 * 
	 * @param className
	 *            类名
	 * @return
	 */
	private static String getPath(String className, String logsPath) {
		StringBuffer path = new StringBuffer();
		path.append(logsPath);
		checkPathExists(path);
		path.append("/" + className);
		return path.toString();
	}
	/**
	 * 检查路径是否存在，不存在时创建
	 * 
	 * @param path
	 */
	private static void checkPathExists(StringBuffer path) {
		String thePath = path.toString();
		File file = new File(thePath);
		if (!file.exists()) {
			file.mkdir();
		}
	}
	/**
	 * 记录错误信息日志
	 * 
	 * @param clazz
	 *            调用类
	 * @param  t
	 *            内容
	 */
	public static void error(Class<?> clazz, Throwable t) {
		LogUtil.error(clazz, t, "");
	}
	/**
	 * 记录错误信息日志。
	 * 
	 * @param clazz
	 *            调用类
	 * @param message
	 *            内容
	 */
	public static void error(Class<?> clazz, String message) {
		Logger log = Logger.getLogger(clazz);
		StringBuffer sb = new StringBuffer("ip:").append(Util.getServerIP())
				.append(" rootpath:").append(PropUtil.ROOT_PATH).append(" ")
				.append(message);
		log.error(sb);
	}
	public static void error(Class<?> clazz, Throwable t, String message) {
		Logger log = Logger.getLogger(clazz);
		StringBuffer sb = new StringBuffer("ip:").append(Util.getServerIP())
				.append(" rootpath:").append(PropUtil.ROOT_PATH).append(" ")
				.append(message);
		log.error(sb, t);
	}
	/**
	 * 记录重要信息日志,线上的最低级别，可写极少的重要测试信息。
	 * 
	 * @param clazz
	 *            调用类
	 * @param message
	 *            内容
	 */
	public static void info(Class<?> clazz, String message) {
		Logger log = Logger.getLogger(clazz);
		StringBuffer sb = new StringBuffer("")
				.append(message);
		log.info(sb);
	}
	
}
