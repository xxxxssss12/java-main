package xs.spider.work.service;

import org.springframework.stereotype.Service;
import xs.spider.base.dao.DaoSupport;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.work.bean.ModelAll;

/**
 * Created by xs on 2017/8/9.
 */
@Service
public class ModelAllServiceImpl extends DaoSupportImpl<ModelAll, Integer> implements DaoSupport<ModelAll, Integer> {
}
