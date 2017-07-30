package xs.spider.work.service;

import org.springframework.stereotype.Service;
import xs.spider.base.dao.DaoSupport;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.work.bean.TestBean;

/**
 * Created by xs on 2017/7/29
 */
@Service
public class TestServiceImpl extends DaoSupportImpl<TestBean, Integer> implements DaoSupport<TestBean, Integer> {
}
