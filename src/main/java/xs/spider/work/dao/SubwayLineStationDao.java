package xs.spider.work.dao;

import org.springframework.stereotype.Repository;
import xs.spider.base.dao.DaoSupport;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.work.bean.SubwayLineStation;

/**
 * Created by xs on 2017/9/24
 */
@Repository
public class SubwayLineStationDao extends DaoSupportImpl<SubwayLineStation, Integer> implements DaoSupport<SubwayLineStation, Integer> {
}
