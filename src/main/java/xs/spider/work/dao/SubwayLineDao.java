package xs.spider.work.dao;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import xs.spider.base.dao.DaoSupport;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.work.bean.SubwayLine;

/**
 * Created by xs on 2017/9/24
 */
@Repository
public class SubwayLineDao extends DaoSupportImpl<SubwayLine, Integer> implements DaoSupport<SubwayLine, Integer> {
}
