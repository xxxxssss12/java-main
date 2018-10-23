package xs.spider.base.util;

import org.slf4j.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class Util {
	private static final Logger log = LogUtil.getLogger(Util.class);
	public static int getIntVal(String str) {
		return getIntVal(str, -1);
	}
	public static int getIntVal(String str, int i) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			log.error("getIntVal error!", e);
			return i;
		}
	}
	public static BigDecimal getBigVal(Object obj) {
		try {
			return new BigDecimal(null2string(obj));
		} catch (Exception e) {
			log.error("getBigVal error!", e);
			return BigDecimal.ZERO;
		}
	}
	/**
	 * 判断字符串是否为空
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		if (str!=null && str.length()>0){
			return false;
		} else {
			return true;
		}
	}
	/**
	 * 判断内置对象是否为空（Integer、String）
	 * @param obj
	 * @return
	 */
	public static boolean isBlank(Object obj) {
		if (obj!=null && obj.toString().length()>0){
			return false;
		} else {
			return true;
		}
	}
	/**
	 * map转url地址后的参数
	 * @param urlparamMap
	 * @return
	 */
	public static String map2urlparam(Map<String , ? extends Object> urlparamMap) {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, ? extends Object> entry : urlparamMap.entrySet()) {
			sb.append("&" + entry.getKey() + "=" + entry.getValue());
		}
		return sb.toString();
	}
	/**
	 * 返回服务器IP
	 * 
	 * @return
	 */
	public static String getServerIP() {
		boolean isWindowsOS = false;
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().indexOf("windows") > -1) {
			isWindowsOS = true;
		}
		String sIP = "";
		InetAddress ip = null;
		try { // 如果是Windows操作系统
			if (isWindowsOS) {
				ip = InetAddress.getLocalHost();
			} else {// 如果是Linux操作系统
				boolean bFindIP = false;
				Enumeration<NetworkInterface> netInterfaces = NetworkInterface
						.getNetworkInterfaces();
				while (netInterfaces.hasMoreElements()) {
					if (bFindIP) {
						break;
					}
					NetworkInterface ni = netInterfaces
							.nextElement();
					// ----------特定情况，可以考虑用ni.getName判断
					// 遍历所有ip
					Enumeration<InetAddress> ips = ni.getInetAddresses();
					while (ips.hasMoreElements()) {
						ip = ips.nextElement();
						if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
						// 127.开头的都是lookback地址
								&& ip.getHostAddress().indexOf(":") == -1) {
							bFindIP = true;
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != ip) {
			sIP = ip.getHostAddress();
		}
		return sIP;
	}
	public static String null2string(Object obj) {
		if (obj == null) return "";
		else return obj.toString();
	}
	/**
	 * 获取用户真实IP地址，不使用request.getRemoteAddr()的原因是有可能用户使用了代理软件方式避免真实IP地址,
	 * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值
	 *
	 * @return ip
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		System.out.println("x-forwarded-for ip: " + ip);
		if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个ip值，第一个ip才是真实ip
			if( ip.indexOf(",")!=-1 ){
				ip = ip.split(",")[0];
			}
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
			System.out.println("Proxy-Client-IP ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
			System.out.println("WL-Proxy-Client-IP ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
			System.out.println("HTTP_CLIENT_IP ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			System.out.println("HTTP_X_FORWARDED_FOR ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
			System.out.println("X-Real-IP ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			System.out.println("getRemoteAddr ip: " + ip);
		}
		System.out.println("获取客户端ip: " + ip);
		return ip;
	}
	/**
	 * 获取请求中的信息
	 * @return*/
	public static Map<String, Object> getrequestInfo(ServletRequest request) {
		HttpServletRequest httpreq = (HttpServletRequest) request;
		//ȡcookies
		Cookie[] cookies = httpreq.getCookies();
		Map<String, String> cookiemap = new HashMap<String, String>();
		if (!isBlank(cookies)) {
			for (Cookie cookie : cookies) {
				String name = cookie.getName();
				String value = cookie.getValue() + ";path=" + cookie.getPath();
				cookiemap.put(name, value);
			}
		}
		Enumeration<String> headernames = httpreq.getHeaderNames();
		//ȡheader
		Map<String, String> headermap = new HashMap<String, String>();
		while (headernames.hasMoreElements()) {
			String name = headernames.nextElement();
			String value = httpreq.getHeader(name);
			headermap.put(name, value);
		}
		//ȡparam
		Map<String, String[]> tempparammap = httpreq.getParameterMap();
		Map<String, String> parammap = new HashMap<String , String>();
		if (!isBlank(tempparammap)) {
			for (Entry<String,String[]> entry : tempparammap.entrySet()) {
				parammap.put(entry.getKey(), entry.getValue()[0]);
			}
		}
		Map<String, Object> all = new HashMap<String, Object>();
		all.put("cookies", cookiemap);
		all.put("headers", headermap);
		all.put("params", parammap);
		return all;
	}
	public static String toLowFirst(String str) {
		try {
			if (isBlank(str)) return str;
			Character c = str.charAt(0);
			if (c.compareTo('A') >= 0 && c.compareTo('Z') <= 0) {
				int i = c.charValue() + 32;
				char c1 = (char) i;
				return c1 + str.substring(1);
			}
		} catch (Exception e) {
			return str;
		}
		return str;
	}

	public static String toUpFirst(String str) {
		try {
			if (isBlank(str)) return str;
			Character c = str.charAt(0);
			if (c.compareTo('a') >= 0 && c.compareTo('z') <= 0) {
				int i = c.charValue() - 32;
				char c1 = (char) i;
				return c1 + str.substring(1);
			}
		} catch (Exception e) {
			return str;
		}
		return str;
	}

	/**
	 * 获取2位16进制随机数
	 * @return
     */
	public static String getRandomHex() {
		Random ran = new Random();
		int base = ran.nextInt(256);
		String hex = Integer.toHexString(base).toLowerCase();
		return hex;
	}
	/**
	 * <p>Title:bytesToHexString </p>
	 * <p>Description: 将要读取文件头信息的文件的byte数组转换成string类型表示 </p>
	 * @param src 要读取文件头信息的文件的byte数组
	 * @return  文件头信息
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder builder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		String hv;
		for (int i = 0; i < src.length; i++) {
			// 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
			hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
			if (hv.length() < 2) {
				builder.append(0);
			}
			builder.append(hv);
		}
		//System.out.println(builder.toString());
		return builder.toString();
	}

	public static String streamToStr(InputStream is, String encode) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, encode));
			String s; // 依次循环，至到读的值为空
			StringBuilder sb = new StringBuilder();
			while ((s = reader.readLine()) != null) {
				sb.append(s);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String stackTrace() {
		StringBuilder sb = new StringBuilder();
		StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
		if (stackElements != null) {
			for (int i = 0; i < stackElements.length; i++) {
				sb.append(stackElements[i].getClassName()).append(".")
						.append(stackElements[i].getMethodName())
						.append(":").append(stackElements[i].getLineNumber()).append(System.getProperty("line.separator"));
			}
		}
		return sb.toString();
	}
}
