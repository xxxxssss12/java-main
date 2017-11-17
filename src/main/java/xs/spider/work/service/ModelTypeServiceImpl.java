package xs.spider.work.service;

import org.springframework.stereotype.Service;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.work.bean.ModelType;
import xs.spider.work.dao.ModelTypeDao;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by xs on 2017/10/22
 */
@Service
public class ModelTypeServiceImpl extends DaoSupportImpl<ModelType, Integer> {
    @Resource
    private ModelTypeDao modelTypeDao;

    public List<ModelType> getAll() {
        return modelTypeDao.getList(new ModelType(), " id asc ");
    }
}
