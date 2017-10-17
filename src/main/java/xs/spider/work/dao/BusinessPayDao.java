package xs.spider.work.dao;

import org.springframework.stereotype.Repository;
import xs.spider.base.dao.DaoSupport;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.work.bean.BusinessPay;

/**
 * Created by xs on 2017/10/17
 */
@Repository
public class BusinessPayDao extends DaoSupportImpl<BusinessPay, Integer> implements DaoSupport<BusinessPay, Integer> {
}
