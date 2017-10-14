package xs.spider.work.service;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xs.spider.base.util.Util;
import xs.spider.work.bean.User;
import xs.spider.work.dao.UserDao;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by xs on 2017/10/12
 */
@Service
public class UserServiceImpl {
    @Autowired
    private UserDao userDao;
    Map<Integer, Set<String>> levelPermissionLevel;

    private static final String P_LOGOUT = "doLogout";
    private static final String P_REFRESH = "refresh";
    private static final String P_SELECT = "select";
    private static final String P_UPDATE = "update";
    private static final String P_DELETE = "delete";
    private static final String P_INSERT = "insert";
    @PostConstruct
    public void init() {
        levelPermissionLevel = new HashMap<>();
        Set<String> level1Set = Sets.newHashSet(P_LOGOUT, P_REFRESH, P_SELECT);
        Set<String> level2Set = Sets.newHashSet(P_LOGOUT, P_REFRESH, P_SELECT, P_UPDATE);
        Set<String> level3Set = Sets.newHashSet(P_LOGOUT, P_REFRESH, P_SELECT, P_UPDATE, P_INSERT);
        Set<String> level4Set = Sets.newHashSet(P_LOGOUT, P_REFRESH, P_SELECT, P_UPDATE, P_INSERT, P_DELETE);
        levelPermissionLevel.put(1, level1Set);
        levelPermissionLevel.put(2, level2Set);
        levelPermissionLevel.put(3, level3Set);
        levelPermissionLevel.put(4, level4Set);
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
}
