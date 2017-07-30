package xs.spider.work.service;

import org.springframework.stereotype.Service;
import xs.spider.base.dao.DaoSupport;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.work.bean.ComeRecord;

/**
 * Created by xs on 2017/7/30
 */
@Service
public class ComeRecordService extends DaoSupportImpl<ComeRecord, Integer> implements DaoSupport<ComeRecord, Integer> {
    public Integer getMaxScore() {
        String sql = "SELECT max(maxScore) From come_record t";
        return getJdbcTemplate().queryForObject(sql, Integer.class);
    }
}
