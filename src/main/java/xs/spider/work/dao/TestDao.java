package xs.spider.work.dao;

import org.springframework.stereotype.Repository;
import xs.spider.base.dao.DaoSupport;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.work.bean.TestBean;

/**
 * Created by xs on 2017/7/29
 */
@Repository
public class TestDao extends DaoSupportImpl<TestBean, Integer> implements DaoSupport<TestBean, Integer> {
}
