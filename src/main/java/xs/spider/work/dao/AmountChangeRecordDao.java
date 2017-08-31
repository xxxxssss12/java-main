package xs.spider.work.dao;

import org.springframework.stereotype.Repository;
import xs.spider.base.dao.DaoSupport;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.work.bean.AmountChangeRecord;

/**
 * Created by xs on 2017/8/30
 */
@Repository
public class AmountChangeRecordDao extends DaoSupportImpl<AmountChangeRecord, Integer> implements DaoSupport<AmountChangeRecord, Integer> {
}
