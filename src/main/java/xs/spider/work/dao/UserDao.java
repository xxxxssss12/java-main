package xs.spider.work.dao;

import org.springframework.stereotype.Repository;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.Util;
import xs.spider.work.bean.User;

/**
 * Created by xs on 2017/4/17.
 */
@Repository
public class UserDao extends DaoSupportImpl<User, Integer> {

    public User getUserByUsername(String username) {
        if (Util.isBlank(username)) return null;
        User user = new User();
        user.setUsername(username);
        try {
            return get(user);
        } catch (Exception e) {
            LogUtil.error(getClass(), e);
            return null;
        }
    }
}
