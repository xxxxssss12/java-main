package xs.spider.work.dao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.base.util.BeanUtil;
import xs.spider.work.bean.Menu;

import java.util.List;

/**
 * Created by xs on 2017/10/15
 */
@Repository
public class MenuDao extends DaoSupportImpl<Menu, Integer> {
    public List<Menu> getListByRoleId(Integer roleId) {
        String sql = "SELECT t.* from " + BeanUtil.getTableName(Menu.class) + " t " +
                " INNER JOIN tb_role_menu t1 on t1.menuId=t.id " +
                " WHERE t1.roleId = ? " +
                " ORDER BY t.showOrder asc";
        List<Menu> menuList = getJdbcTemplate().query(sql, new Object[]{roleId}, new BeanPropertyRowMapper<>(Menu.class));
        return menuList;
    }
}
