package xs.spider.work.dao;

import org.springframework.stereotype.Repository;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.base.util.LogUtil;
import xs.spider.work.bean.ContentDetail;

/**
 * Created by xs on 2017/9/30.
 */
@Repository
public class ContentDetailDao extends DaoSupportImpl<ContentDetail,Integer> {
    public ContentDetail getByTitleId(Integer titleId) {
        if (titleId == null) return null;
        ContentDetail detail = new ContentDetail();
        detail.setTitleid(titleId);
        detail = get(detail);
        if (detail == null) LogUtil.info(getClass(), "getByTitleId...查询出来为null");
        return detail;
    }
}
