package xs.spider.work.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.Util;

/**
 * Created by xs on 2017/4/17.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    @RequestMapping("/doLogin")
    public ResultInfo doLogin(String username, String password) {
        if (Util.isBlank(username) || Util.isBlank(password)) {
            return new ResultInfo(-1, "缺少必要参数");
        }
        int timeOut = 1000*60*24*60;
        Subject subject = null;
        ResultInfo ri = null;
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            subject = SecurityUtils.getSubject();
            subject.getSession().setTimeout(timeOut);//1天过期
            subject.login(token);
            ri = new ResultInfo(1, "成功");
        }  catch (IncorrectCredentialsException e) {
            ri = new ResultInfo(-1,"用户名或密码错误");
        } catch (ExcessiveAttemptsException e) {
            ri = new ResultInfo(-1,"登录失败次数过多");
        } catch (LockedAccountException e) {
            ri = new ResultInfo(-1,"帐号已被锁定");
        } catch (DisabledAccountException e) {
            ri = new ResultInfo(-1,"帐号已被禁用");
        } catch (ExpiredCredentialsException e) {
            ri = new ResultInfo(-1,"帐号已过期");
        } catch (UnknownAccountException e) {
            ri = new ResultInfo(-1,"用户名或密码错误");
        } catch (UnauthorizedException e) {
            ri = new ResultInfo(-1,"您没有得到相应的授权");
        } catch (Exception e) {
            LogUtil.error(getClass(),e);
            ri = new ResultInfo(-1,"系统发生异常");
        }
        return ri;
    }
}
