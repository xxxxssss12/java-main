package xs.spider.work.controller;

import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.spider.base.bean.ResultInfo;
import xs.spider.work.bean.User;
import xs.spider.work.service.MenuServiceImpl;
import xs.spider.work.service.UserServiceImpl;
import xs.spider.work.shiro.CurrentUserHelper;

/**
 * Created by xs on 2017/10/15
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private MenuServiceImpl menuService;
    @RequestMapping("/getCurrentUserMenu")
    public ResultInfo getCurrentUserMenu() {
        User user = userService.getUserByUsername(CurrentUserHelper.getCurrentUsername());
        if (user == null) return new ResultInfo(-1, "请重新登录！");
        JSONArray menuList = menuService.findUserMenuTree(user);
        if (menuList == null || menuList.isEmpty()) return ResultInfo.buildFail("用户没有配置菜单！");
        return ResultInfo.build(menuList);
    }
    @RequestMapping("/getCurrentUserInfo")
    public ResultInfo getCurrentUserInfo() {
        User user = userService.getUserByUsername(CurrentUserHelper.getCurrentUsername());
        if (user == null) return new ResultInfo(-1, "请重新登录！");
        user.setPassword(null);
        user.setUserLevel(null);
        user.setRoleId(null);
        user.setCreateTime(null);
        user.setId(null);
        return ResultInfo.build(user);
    }
}
