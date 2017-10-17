package xs.spider.base.util;

import com.alibaba.fastjson.JSONObject;
import xs.spider.base.bean.PageBean;

/**
 * 类型转换工具类
 * @author hasee
 *
 */
public class TypeConvUtil {
	/**
	 * 将字符串转换为int，失败返回-1
	 * @param str
	 * @return
	 */
	public static int str2IntCatch(String str) {
		return str2IntCatch(str, -1);
	}
	/**
	 * 将字符串转换为int，失败返回指定值
	 * @param str
	 * @return
	 */
	public static int str2IntCatch(String str, int i) {
		try {
			return Integer.valueOf(str);
		} catch(Exception e) {
			return i;
		}
	}
	/**
	 * 将字符串转换为double，失败返回0
	 * @param str
	 * @return
	 */
	public static double str2DoubleCatch(String str) {
		return str2DoubleCatch(str, 0);
	}
	/**
	 * 将字符串转换为double，失败返回指定值
	 * @param str
	 * @return
	 */
	public static double str2DoubleCatch(String str, int i) {
		// TODO Auto-generated method stub
		return 0;
	}
	public static Object parseStr2(String str, Class<?> clazz) {
		String typename = clazz.getSimpleName();
		if (typename.equals("int") || typename.equals("Integer")) {
			return Integer.valueOf(str);
		} else if (typename.equals("double") || typename.equals("Double")) {
			return Double.valueOf(str);
		} else if (typename.equals("float") || typename.equals("Float")) {
			return Float.valueOf(str);
		} else if (typename.equals("Long") || typename.equals("long")) {
			return Long.valueOf(str);
		}
		return null; 
	}

	public static JSONObject convertPageToLiger(PageBean<?> pageBean) {
		JSONObject obj = new JSONObject();
		obj.put("Total", 0);
		obj.put("page", 1);
		obj.put("pagesize", 10);
		if (pageBean != null && pageBean.getCode() > 0) {
			obj.put("Total", pageBean.getTotal());
			obj.put("page", pageBean.getPageNum());
			obj.put("pagesize", pageBean.getPageSize());
			obj.put("Rows", pageBean.getData());
		}
		return obj;
	}
	public static void main(String[] args) {
		System.out.println(parseStr2("123", double.class));
	}
}
