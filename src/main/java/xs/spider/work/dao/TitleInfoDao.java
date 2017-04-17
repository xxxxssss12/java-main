package xs.spider.work.dao;

import org.springframework.stereotype.Repository;
import xs.spider.base.bean.PageBean;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.base.util.BeanUtil;
import xs.spider.base.util.Util;
import xs.spider.work.bean.TitleInfo;

import java.util.ArrayList;

/**
 * Created by xs on 2017/4/5.
 */
@Repository
public class TitleInfoDao extends DaoSupportImpl<TitleInfo,Integer> {

    public PageBean getPage(String title) throws Exception {
        String temp = Util.null2string(title).trim();
//        if (Util.isBlank(temp)) return getPage(new TitleInfo(), " dtb.createTime desc, dtb.time desc ");
        StringBuffer sql = new StringBuffer();
        sql.append("select * from ").append(BeanUtil.getTableName(TitleInfo.class))
                .append(" t where t.isValid>0 ");
        if (!Util.isBlank(temp)) {
            String[] condition = title.split(" ");
            sql.append(" and (");
            int cnt = 0;
            for (int i = 0; i < condition.length; i++) {
                if (!Util.isBlank(condition[i])) {
                    if (cnt != 0) sql.append(" or ");
                    sql.append(" t.content like '%").append(condition[i]).append("%' ");
                    cnt++;
                }
            }
            sql.append(" ) ");
        }
        sql.append(" order by t.createTime desc, t.time desc ");
        return getPage(sql.toString(),new ArrayList());
    }
}
