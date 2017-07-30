package xs.spider.base.bean;

import com.alibaba.fastjson.annotation.JSONField;
import xs.spider.base.anno.Id;
import xs.spider.base.anno.Table;
import xs.spider.base.anno.UserDefined;
import xs.spider.base.util.BeanUtil;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by hasee on 2016/8/28.
 */
public abstract class BaseEntity implements Serializable {
    /** 排除的属性，该属性是用于序列化 */
    private final String SERIAL_VERSION_UID = "serialVersionUID";
    /** 用户自定义注解 */
    private final Class<? extends Annotation> USERDEFINED_ANNOTATION = UserDefined.class;
    /**
     * 获取所有属性名和属性类型（通过注解进行排除）
     */
    public List<String> gotAttrNames() {
        Class<? extends BaseEntity> clazz = this.getClass();
        Field[] fs = clazz.getDeclaredFields();
        List<String> paramList = new ArrayList<String>();
        for (int i=0; i<fs.length; i++) {
            Field f = fs[i];
            f.setAccessible(true);
            if (!f.isAnnotationPresent(USERDEFINED_ANNOTATION)
                    && !SERIAL_VERSION_UID.equalsIgnoreCase(f.getName())) {
                paramList.add(f.getName());
            }
        }
        if (!this.getClass().getSuperclass().equals(BaseEntity.class)) {
            try {
                BaseEntity baseEntity = (BaseEntity) this.getClass().getSuperclass()
                        .newInstance();
                paramList.addAll(baseEntity.gotAttrNames());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return paramList;
    }
    /**
     * 根据属性名获取属性的值
     *
     * @param name
     * @return
     * @throws Exception
     */
    @JSONField(serialize=false)
    public Object getAttributeValue(String name) {
        if (name==null || name.length()==0) {
            return null;
        }
        Object value = BeanUtil.getProperty(this, name);
        if (null != value && value instanceof String) {
            return value.toString().trim();
        }
        return value;
    }
    /**
     * 设置属性的值
     *
     * @param name
     * @param value
     * @throws Exception
     */
    public void setAttributeValue(String name, Object value) throws Exception {
        BeanUtil.setProperty(this, name, value);
    }

    @JSONField(serialize=false)
    public String getTableName() {
        Table tbAnno = this.getClass().getAnnotation(Table.class);
        if (tbAnno != null) {
            return tbAnno.value();
        }
        return null;
    }
    @JSONField(serialize=false)
    public String getPkName() {
        Id idAnno = this.getClass().getAnnotation(Id.class);
        if (idAnno != null)
            return idAnno.value();
        return null;
    }
}
