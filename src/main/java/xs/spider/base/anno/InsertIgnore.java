package xs.spider.base.anno;

import java.lang.annotation.*;

/**
 * Copyright: Copyright (c) 2013 SouFun
 *
 * @Description: 定义注解，用于标注实体类中自定义的字段，
 * 获取所有要加入数据库属性的时候忽略标注了该注解的字段
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface InsertIgnore {

}
