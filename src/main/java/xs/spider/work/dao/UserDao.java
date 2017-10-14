package xs.spider.work.dao;

import org.springframework.stereotype.Component;
import xs.spider.base.dao.DaoSupport;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.work.bean.User;

/**
 * Created by xs on 2017/10/12
 */
@Component
public class UserDao extends DaoSupportImpl<User, Integer> implements DaoSupport<User, Integer> {
}
