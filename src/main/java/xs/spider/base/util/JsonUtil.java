package xs.spider.base.util;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hasee on 2016/10/13.
 */
public class JsonUtil {
    /**
     * 将bean转换为json对象 要转换的bean如果为对象，对象中的属性必须有public的get方法
     *
     * @param obj
     *            要转换的对象
     * @return
     * @throws Exception
     */
    public static String beanToJson(Object obj) {
        try {
            return beanToJson(obj, "yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 将bean转换为json对象 要转换的bean如果为对象，对象中的属性必须有public的get方法
     *
     * @param obj
     *            要转换的对象
     * @param isReplaceSpecialChar
     * @return
     * @throws Exception
     */
    public static String beanToJson(Object obj,boolean isReplaceSpecialChar) throws Exception {
        return beanToJson(obj, "yyyy-MM-dd HH:mm:ss",isReplaceSpecialChar);
    }

    /**
     * 将bean转换为json对象 要转换的bean如果为对象，对象中的属性必须有public的get方法
     *
     * @param obj
     *            要转换的对象
     * @return
     * @throws Exception
     */
    public static String beanToJson(Object obj, String dateFormatStr)
            throws Exception {
        return beanToJson(obj, dateFormatStr,true);

    }

    /**
     * 是否替换特殊字符
     * @param obj
     * @param dateFormatStr
     * @param isReplaceSpecialChar
     * @return
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    public static String beanToJson(Object obj, String dateFormatStr,boolean isReplaceSpecialChar)
            throws Exception {
        if (null == obj) {
            return "";
        }
        String json = "";
        try {
//            ObjectMapper mapper = new ObjectMapper();
            // 个性化设置
//            mapper.configure(SerializationConfig.Feature.WRITE_NULL_PROPERTIES,
//                    false);
//            mapper.getSerializationConfig().setSerializationInclusion(
//                    JsonSerialize.Inclusion.NON_NULL);
            // 格式化日期
//            mapper.setDateFormat(new SimpleDateFormat(dateFormatStr));
//            writer = new StringWriter();
//            gen = new JsonFactory().createJsonGenerator(writer);
//            mapper.writeValue(gen, obj);
//            json = writer.toString();
            json = JSON.toJSONStringWithDateFormat(obj, "yyyy-MM-dd HH:mm:ss.SSS");
        } catch (Exception e) {
        } finally {
        }
        if(isReplaceSpecialChar){
            json = json.replaceAll("'", "&#39;");
        }
        return json;

    }

    /**
     * json转换list 例：this.json2List(josn, Map.class); this.json2List(josn,
     * EWfNodeBase.class);
     *
     * @param json
     *            json字符串
     * @param elementClass
     *            转化的类型
     * @return
     * @author zhaolimin
     * @date 2014-6-10 下午3:07:50
     *
     */
    public static List<?> jsonToList(String json, Class<?> elementClass)
            throws Exception {
        if (!Util.isBlank(json)) {
            if (!json.startsWith("[")) {
                json = "[" + json + "]";
            }
            JSON.parseArray(json, elementClass);
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,
//                    true);
//            JavaType javaType = mapper.getTypeFactory()
//                    .constructParametricType(ArrayList.class, elementClass);
//            return mapper.readValue(json, javaType);
        }
        return null;
    }


    /**
     * json转map
     * @param json
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jsonToMap(String json) throws Exception {
        List<Map<String, Object>> list = (List<Map<String, Object>>) jsonToList(
                json, Map.class);
        if (null != list) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 数组转json
     *
     * @author zhaoheng
     * @date 2014-10-24下午5:45:59
     * @param arrays
     * @return
     * @throws Exception
     */
    public static String arrayToJson(Object[] arrays) throws Exception {

        return beanToJson(arrays);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseJSON2Map(String jsonStr)
            throws Exception {
//        List<Map<String, Object>> list = (List<Map<String, Object>>) jsonToList(
//                jsonStr, Map.class);
//
//        if (null != list && list.size() > 0) {
//            return list.get(0);
//        }
        Map<String, Object> map = JSON.parseObject(jsonStr, Map.class);
        return map;
    }


}
