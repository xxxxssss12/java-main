package xs.spider.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import xs.spider.base.init.Log4jInit;
import xs.spider.base.util.ApplicationContextHandle;
import xs.spider.base.util.JsonUtil;
import xs.spider.work.bean.TitleInfo;
import xs.spider.work.dao.TitleInfoDao;

import java.util.Date;

/**
 * Created by xs on 2017/4/5.
 */
public class ATest {
    private static ClassPathXmlApplicationContext context;
    @Before
    public void before() {
        Log4jInit.init1();
        context = new ClassPathXmlApplicationContext(
                new String[]{
                        "classpath:config/applicationContext*.xml"
                }
        );
        context.start();
    }
    @Test
    public void testDao() throws Exception {
        TitleInfoDao titleInfoDao = (TitleInfoDao) ApplicationContextHandle.getBean("titleInfoDao");
        TitleInfo info = titleInfoDao.get(2506);
        System.out.println(JsonUtil.beanToJson(info));
        TitleInfo bean = new TitleInfo();
        bean.setPagenum(-1);
        bean.setUrl("test");
        bean.setContent("hehe");
        bean.setTime(new Date());
        bean.setId(1);
        titleInfoDao.save(bean);
    }

}
