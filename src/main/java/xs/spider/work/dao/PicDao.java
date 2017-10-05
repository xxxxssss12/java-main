package xs.spider.work.dao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.work.bean.Pic;

import java.util.List;

/**
 * Created by xs on 2017/9/30.
 */
@Repository
public class PicDao extends DaoSupportImpl<Pic,Integer> {
    public List<Pic> getByTitleId(Integer titleId) {
        String sql = "select t.* from pic t INNER JOIN title_pic t1 on t1.picId = t.id where t1.titleId = ?";
        return getJdbcTemplate().query(sql, new Object[]{titleId}, new BeanPropertyRowMapper<Pic>(Pic.class));
    }
}
