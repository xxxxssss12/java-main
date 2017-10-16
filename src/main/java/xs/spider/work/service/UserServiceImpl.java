package xs.spider.work.service;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xs.spider.base.util.Util;
import xs.spider.work.bean.User;
import xs.spider.work.dao.UserDao;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
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
//    private static final String P_REFRESH = "refresh";
    private static final String P_SELECT = "select,get,find";
    private static final String P_UPDATE = "update,refresh";
    private static final String P_DELETE = "delete,remove";
    private static final String P_INSERT = "insert,save";
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
}
