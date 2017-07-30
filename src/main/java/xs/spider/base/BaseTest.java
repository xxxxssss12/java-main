package xs.spider.base;

import org.apache.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by xs on 2017/7/30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:config/applicationContext.xml"})
public abstract class BaseTest {

    protected Logger log = Logger.getLogger(this.getClass());

}
