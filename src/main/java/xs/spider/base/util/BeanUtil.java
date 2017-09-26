package xs.spider.base.util;

import org.apache.log4j.Logger;
import xs.spider.base.anno.Column;
import xs.spider.base.anno.Table;
import xs.spider.base.anno.UserDefined;
import xs.spider.base.bean.BaseEntity;
import xs.spider.work.bean.TitleInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 反射辅助bean操作
 * @author xs
 *
 */
public class BeanUtil {
	private static final Logger log = Logger.getLogger(BeanUtil.class);
	private static BeanUtil bhelp = new BeanUtil();
	protected static final Object NULL_ARGUMENTS[] = new Object[0];
	/**
	 * 通过属性名获取属性的值
	 * @param bean 实例
	 * @param propertyName 属性名
	 * @return
	 * @throws Exception
	 */
	public static Object getProperty(Object bean, String propertyName)
	{
		Method method = null;
		try {
			method = bhelp.getMethod(bean.getClass(), propertyName, false);
			if (propertyName != null && method == null)
				return null;
		} catch (Exception e) {
			String errStr = (new StringBuilder()).append(
					"Failed to get property: ").append(propertyName).toString();
			throw new RuntimeException(errStr, e);
		}
		if (method == null)
			return null;
		try {
			return method.invoke(bean, NULL_ARGUMENTS);
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}
	/**
	 * 获取属性的get、set方法
	 * @param clazz 实例
	 * @param propertyName 属性名
	 * @param isSetMethod true：获取set方法；false：获取get方法
	 * @return
	 * @throws Exception
	 */
	private static Method getMethod(Class clazz, String propertyName,	boolean isSetMethod) throws Exception {
		try {
			Method method = null;
			String methodname = propertyName.substring(1);
			if (isSetMethod) {
				methodname = "set" + ((propertyName.charAt(0) + "").toUpperCase()) + methodname;
				//通过属性名获取属性的类型
				Field field = clazz.getDeclaredField(propertyName);
				method = clazz.getDeclaredMethod(methodname, field.getType());
			} else {
				methodname = "get" + ((propertyName.charAt(0) + "").toUpperCase()) + methodname;
				method = clazz.getDeclaredMethod(methodname);
			}

			return method;
		} catch (Exception e) {
			log.error("getMethod err!" + propertyName);
			return null;
		}
	}
	/**
	 * 使用属性的set方法给指定属性赋值
	 * @param bean 实例
	 * @param propertyName 属性名称
	 * @param value 值
	 * @throws Exception
	 */
	public static void setProperty(Object bean, String propertyName,
			Object value) {
		Method method;
		try {
			method = bhelp.getMethod(bean.getClass(), propertyName, true);
			if (propertyName != null && method == null)
				return;
		} catch (IllegalArgumentException e) {
			String errStr = (new StringBuilder()).append(
					"Failed to set property: ").append(propertyName).append(
					" at bean: ").append(bean.getClass().getName()).append(
					" with value:").append(value).append(" type:").append(
					value != null ? value.getClass().getName() : "null")
					.toString();
			throw new IllegalArgumentException(errStr, e);
		} catch (Exception e) {
			String errStr = (new StringBuilder()).append(
					"Failed to set property: ").append(propertyName).append(
					" at bean: ").append(bean.getClass().getName()).append(
					" with value:").append(value).toString();
			throw new RuntimeException(errStr, e);
		}
		if (method == null)
			return;
		try {
			method.invoke(bean, new Object[]{value});
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * 获取类上指定字段对应列名
	 * @param clazz
	 * @param attrName
	 * @return
	 * @throws Exception
     */
	public static String getColumnByAttrName(Class clazz, String attrName) throws Exception {
		String columnName = null;
		try {
			Field field = clazz.getDeclaredField(attrName);
			Column columnAnno = field.getAnnotation(Column.class);
			if (columnAnno == null) {
				columnName = attrName;
			} else {
				columnName = columnAnno.value();
			}
		} catch (Exception e) {

		}
		return columnName;
	}

	public static boolean isUserDefine(Class<? extends BaseEntity> clazz, String name) {
		try {
			if (clazz == null || name == null) {
				return false;
			}
			Field field = clazz.getDeclaredField(name);
			UserDefined userDefineAnno = field.getAnnotation(UserDefined.class);
			if (userDefineAnno == null)
				return false;
			else
				return true;
		} catch (Exception e) {
			log.error("isUserDefine Error!", e);
			return false;
		}
	}

	public static Object parseMapToBean(Map<String, Object> map,Class clazz) {
        if (map == null || clazz == null) return null;
        Object bean = null;
        try {
            bean = clazz.newInstance();
        } catch (Exception e) {
            log.error("创建bean失败！", e );
            return null;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            try {
                Method setMethod = getMethod(clazz, key, true);
                if (setMethod == null) {
                    continue;
                } else {
                    setMethod.invoke(bean, entry.getValue());
                }
            } catch (Exception e) {
                log.error("set error", e);
            }
        }
        return bean;
	}

	public static String getTableName(Class<? extends BaseEntity> clazz) {
		if (clazz == null) return null;
		Table tbAnno = clazz.getAnnotation(Table.class);
		if (tbAnno != null) {
			return tbAnno.value();
		}
		return null;
	}
}
