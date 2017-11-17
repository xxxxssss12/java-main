package xs.spider.work.service;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xs.spider.base.bean.PageBean;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.base.util.Util;
import xs.spider.work.bean.User;
import xs.spider.work.dao.UserDao;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by xs on 2017/10/12
 */
@Service
public class UserServiceImpl extends DaoSupportImpl<User, Integer> {
    @Autowired
    private UserDao userDao;
    Map<Integer, Set<String>> levelPermissionLevel;

    private static final String P_LOGOUT = "doLogout";
//    private static final String P_REFRESH = "refresh";
    private static final String P_SELECT = "select,get,find";
    private static final String P_UPDATE = "update,refresh";
    private static final String P_DELETE = "delete,remove";
    private static final String P_INSERT = "insert,save,add";
    @PostConstruct
    public void init() {
        levelPermissionLevel = new HashMap<>();
        String[] select = P_SELECT.split(",");
        String[] update = P_UPDATE.split(",");
        String[] delete = P_DELETE.split(",");
        String[] insert = P_INSERT.split(",");
        Set<String> level1Set = createSet(select);
        Set<String> level2Set = createSet(select, insert);
        Set<String> level3Set = createSet(select, update, insert);
        Set<String> level4Set = createSet(select, update, insert, delete);
        levelPermissionLevel.put(1, level1Set);
        levelPermissionLevel.put(2, level2Set);
        levelPermissionLevel.put(3, level3Set);
        levelPermissionLevel.put(4, level4Set);
    }

    private Set<String> createSet(String[]... permissions) {
        Set<String> set = new HashSet<>();
        for (String[] permission : permissions) {
            if (permission != null && permission.length > 0) {
                for (String per : permission) {
                    set.add(per);
                }
            }
        }
        set.add(P_LOGOUT);
        return set;
    }

    public User getUserByUsername(String username) {
        if (Util.isBlank(username)) return null;
        User user = new User();
        user.setUsername(username);
        return userDao.get(user);
    }

    public Set<String> getPermission(String username) {
        if (Util.isBlank(username)) return null;
        return getPermission(getUserByUsername(username));
    }

    public Set<String> getPermission(User user) {
        if (user == null) return null;
        if (user.getUserLevel() == null) return null;
        return levelPermissionLevel.get(user.getUserLevel());
    }

    public PageBean<User> getByPage(String username, String realName) {
        StringBuilder sql = new StringBuilder("SELECT t.*,t1.name roleName FROM tb_user t inner join tb_role t1 on t1.id = t.roleId where 1=1");
        List<Object> list = new ArrayList<>();
        if (!Util.isBlank(realName)) {
            sql.append(" AND t.realName like '%").append(realName).append("%'");
        }
        if (!Util.isBlank(username)) {
            sql.append(" AND t.username=? ");
            list.add(username);
        }
        return userDao.getPage(sql.toString(), list);
    }
}
