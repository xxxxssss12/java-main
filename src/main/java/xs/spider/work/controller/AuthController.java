package xs.spider.work.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.config.ConfigProvider;
import xs.spider.base.util.ExceptionWrite;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.Util;
import xs.spider.work.bean.User;
import xs.spider.work.service.UserServiceImpl;
import xs.spider.work.shiro.CurrentUserHelper;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xs on 2017/10/13
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserServiceImpl userService;

    private static Integer timeOut = ConfigProvider.getInt("session.invalidate.time", 1000*60*60*24);
    private static String failKey = "auth:dologin:ldap:auth:fail:";
    private static ConcurrentHashMap<String, JSONObject> failCntMap = new ConcurrentHashMap<>();
    @RequestMapping("/doLogin")
    public ResultInfo doLogin(String username, String password) {
        ResultInfo ri = checkUser(username, password);
        if (ri.getCode() <= 0) return ri;
        User user = (User) ri.getData();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject subject = null;
        try {
            subject = SecurityUtils.getSubject();
            subject.getSession().setTimeout(timeOut);//1天过期
            subject.login(token);
            if (subject.isAuthenticated()) {
                ri = ResultInfo.build();
            } else {
                ri = ResultInfo.build("用户名或密码错误");
            }
        } catch (IncorrectCredentialsException e) {
            ri = ResultInfo.buildFail("用户名或密码错误");
        } catch (ExcessiveAttemptsException e) {
            ri = ResultInfo.buildFail("登录失败次数过多");
        } catch (LockedAccountException e) {
            ri = ResultInfo.buildFail("帐号已被锁定");
        } catch (DisabledAccountException e) {
            ri = ResultInfo.buildFail("帐号已被禁用");
        } catch (ExpiredCredentialsException e) {
            ri = ResultInfo.buildFail("帐号已过期");
        } catch (UnknownAccountException e) {
            ri = ResultInfo.buildFail("用户名或密码错误");
        } catch (UnauthorizedException e) {
            ri = ResultInfo.buildFail("您没有得到相应的授权:"+e.getMessage());
        } catch (Exception e) {
            LogUtil.error(getClass(), "username=" + username + ";password=" + password + ExceptionWrite.get(e));
            ri =  ResultInfo.buildFail("系统发生异常");
        }
        if (ri.getCode() == 1) failCntMap.remove(user.getUsername());
        else if (user != null) incrFailTime(user.getUsername());
        return ri;
    }
    @RequestMapping("/doLogout")
    public ResultInfo doLogout() {
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.logout();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return ResultInfo.build();
    }

    private ResultInfo checkUser(String username, String password) {
        if (Util.isBlank(username) || Util.isBlank(password))
            return ResultInfo.buildFail("参数缺失");
        String failTime = getFailTime(username);
        if (!Util.isBlank(failTime) && Integer.parseInt(failTime) > 5)
            return ResultInfo.buildFail("失败次数过多");
        User user = userService.getUserByUsername(username);
        if (user == null)
            return ResultInfo.buildFail("用户名或密码错误");
        return ResultInfo.build(user);
    }
    private String getFailTime(String username) {
        JSONObject obj = failCntMap.get(username);
        if (obj == null || obj.getLong("expireTime") < System.currentTimeMillis()) {
            failCntMap.remove(username);
            return "0";
        }
        return obj.getString("cnt");
    }

    private void incrFailTime(String username) {
        JSONObject obj = failCntMap.get(username);
        if (obj == null || obj.getLong("expireTime") < System.currentTimeMillis()) {
            obj = new JSONObject();
            obj.put("cnt", "1");
            obj.put("expireTime", System.currentTimeMillis() + 1000 * 60 * 60 * 2);
            failCntMap.put(username, obj);
        } else {
            obj.put("cnt", obj.getIntValue("cnt") + 1);
        }

    }
    @RequestMapping("/refreshFailCnt")
    public ResultInfo refreshFailCnt(String username) {
        if (Util.isBlank(username)) return ResultInfo.buildFail("缺少参数");
        String name = CurrentUserHelper.getCurrentUsername();
        if ("xiongshun".equals(name) || "xiongguangyue".equals(name)) {
            failCntMap.remove(username);
            return ResultInfo.build();
        } else {
            return ResultInfo.buildFail("fail");
        }
    }
}
