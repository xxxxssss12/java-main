package xs.spider.work.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.config.ConfigProvider;
import xs.spider.base.util.MD5Util;
import xs.spider.base.util.TypeConvUtil;
import xs.spider.base.util.Util;
import xs.spider.work.bean.ModelDetail;
import xs.spider.work.bean.Role;
import xs.spider.work.bean.User;
import xs.spider.work.service.MenuServiceImpl;
import xs.spider.work.service.RoleServiceImpl;
import xs.spider.work.service.UserServiceImpl;
import xs.spider.work.shiro.CurrentUserHelper;

import java.util.Date;
import java.util.List;

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
    @Autowired
    private RoleServiceImpl roleService;
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
    @RequestMapping("/getByPage")
    public JSONObject getPage(String username, String realName) {
        return TypeConvUtil.convertPageToLiger(userService.getByPage(username, realName));
    }

    @RequestMapping("/add")
    public ResultInfo add(User user) {
        User currentUser = userService.getUserByUsername(CurrentUserHelper.getCurrentUsername());
        if (currentUser == null) return new ResultInfo(-1, "请重新登录！");
        if (currentUser.getRoleId().intValue() != ConfigProvider.getInt("admin.role.id")) return ResultInfo.buildFail("没有操作权限");
        if (user == null
                || Util.isBlank(user.getUsername())
                || Util.isBlank(user.getRoleId())
                || Util.isBlank(user.getPassword())
                || Util.isBlank(user.getUserLevel())
                || Util.isBlank(user.getRealName()))
            return ResultInfo.buildFail("缺少必要参数");
        User existUser = userService.getUserByUsername(user.getUsername());
        if (existUser != null) return ResultInfo.buildFail("用户名不能重复");
        user.setPassword(MD5Util.MD5(user.getPassword()));
        user.setCreateTime(new Date());
        userService.save(user);
        return ResultInfo.build();
    }

    @RequestMapping("/batchUpdate")
    public ResultInfo batchUpdate(String userDetailList) {
        User currentUser = userService.getUserByUsername(CurrentUserHelper.getCurrentUsername());
        if (currentUser == null) return new ResultInfo(-1, "请重新登录！");
        if (currentUser.getRoleId().intValue() != ConfigProvider.getInt("admin.role.id")) return ResultInfo.buildFail("没有操作权限");
        if (Util.isBlank(userDetailList)) {
            return ResultInfo.buildFail("缺少必要参数");
        }
        List<User> userList = JSON.parseArray(userDetailList, User.class);
        for (User user : userList) {
//            if (!Util.isBlank(user.getRoleName())) {
//                Role role = new Role();
//                role.setName(user.getRoleName());
//                role = roleService.get(role);
//                if (role != null) user.setRoleId(role.getId());
//            }
            user.setRoleName(null);
            user.setPassword(null);
            userService.update(user, false);
        }
        return ResultInfo.build();
    }

    @RequestMapping("/delete")
    public ResultInfo delete(Integer id) {
        User currentUser = userService.getUserByUsername(CurrentUserHelper.getCurrentUsername());
        if (currentUser == null) return new ResultInfo(-1, "请重新登录！");
        if (currentUser.getRoleId().intValue() != ConfigProvider.getInt("admin.role.id")) return ResultInfo.buildFail("没有操作权限");
        if (Util.isBlank(id)) return ResultInfo.buildFail("缺少必要参数");
        userService.delete(id);
        return ResultInfo.build();
    }

    @RequestMapping("/updatePassword")
    public ResultInfo updatePassword(Integer id, String password) {
        User currentUser = userService.getUserByUsername(CurrentUserHelper.getCurrentUsername());
        if (currentUser == null) return new ResultInfo(-1, "请重新登录！");
        if (currentUser.getRoleId().intValue() != ConfigProvider.getInt("admin.role.id"))
            return ResultInfo.buildFail("没有操作权限");
        if (Util.isBlank(id) || Util.isBlank(password)) return ResultInfo.buildFail("缺少必要参数");
        if (password.length() < 6) return ResultInfo.buildFail("密码错误");
        User user = new User();
        user.setId(id);
        user.setPassword(MD5Util.MD5(password));
        userService.update(user, false);
        return ResultInfo.build();
    }
}
