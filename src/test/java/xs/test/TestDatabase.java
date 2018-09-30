package xs.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import xs.spider.work.bean.TestBean;
import xs.spider.work.service.TestServiceImpl;

import java.util.Date;

/**
 * Created by xs on 2018/9/28
 */
public class TestDatabase extends BaseTest {

    @Autowired
    TestServiceImpl testService;

    @Test
    public void testInsert() {
        for (int i=0; i<1000; i++) {
            TestBean bean = new TestBean();
            bean.setCreateTime(new Date());
            bean.setTestC("test" + i);
            bean.setUpdateTime(new Date());
            testService.save(bean);
            System.out.println("保存完毕！");
        }
    }
}
