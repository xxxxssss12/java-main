package xs.spider.work.service;

import org.springframework.stereotype.Service;
import xs.spider.base.bean.PageBean;
import xs.spider.base.dao.DaoSupport;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.base.util.Util;
import xs.spider.work.bean.ModelAll;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xs on 2017/8/9.
 */
@Service
public class ModelAllServiceImpl extends DaoSupportImpl<ModelAll, Integer> implements DaoSupport<ModelAll, Integer> {
    public PageBean<ModelAll> getPage(String modelNo) {
        StringBuilder sql = new StringBuilder("SELECT t.* FROM tb_model_all t WHERE 1=1 ");
        List<Object> list = new ArrayList<>();
        if (!Util.isBlank(modelNo)) {
            sql.append(" AND modelNo like '%").append(modelNo).append("%'");
        }
        return getPage(sql.toString(), list);
    }
}
