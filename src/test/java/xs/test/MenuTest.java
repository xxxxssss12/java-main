package xs.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.base.util.BaseTest;
import xs.spider.work.dao.UserDao;
import xs.spider.work.service.MenuServiceImpl;
import xs.spider.work.service.UserServiceImpl;

/**
 * Created by xs on 2017/10/15
 */
public class MenuTest extends BaseTest {
    @Autowired
    private MenuServiceImpl menuService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserDao userDao;
    @Test
    public void testGetMenu() {
        System.out.println(menuService.findUserMenuTree(userService.getUserByUsername("xiongshun")).toJSONString());
    }

    @Test
    public void test1() {
        userDao.insert();
    }
}
