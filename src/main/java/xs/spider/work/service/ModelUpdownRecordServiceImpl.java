package xs.spider.work.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.work.bean.ModelUpdownRecord;
import xs.spider.work.dao.ModelUpdownRecordDao;

/**
 * Created by xs on 2017/10/29
 */
@Service
public class ModelUpdownRecordServiceImpl extends DaoSupportImpl<ModelUpdownRecord, Integer> {
    @Autowired
    private ModelUpdownRecordDao modelUpdownRecordDao;

}
