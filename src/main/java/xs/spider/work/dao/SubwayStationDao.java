package xs.spider.work.dao;

import org.springframework.stereotype.Repository;
import xs.spider.base.dao.DaoSupport;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.work.bean.SubwayStation;

/**
 * Created by xs on 2017/9/24
 */
@Repository
public class SubwayStationDao extends DaoSupportImpl<SubwayStation, Integer> implements DaoSupport<SubwayStation, Integer> {
}
