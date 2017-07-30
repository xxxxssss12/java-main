package xs.spider.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import xs.spider.base.BaseTest;
import xs.spider.work.service.ComeRecordService;

/**
 * Created by xs on 2017/7/30
 */
public class ComeTest extends BaseTest {
    @Autowired
    private ComeRecordService comeRecordService;
    @Test
    public void test1() {
        System.out.println(comeRecordService.getMaxScore());
    }
}
