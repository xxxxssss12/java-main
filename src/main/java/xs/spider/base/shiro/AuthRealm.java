package xs.spider.base.shiro;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.Util;
import xs.spider.work.bean.User;
import xs.spider.work.dao.UserDao;

import javax.annotation.Resource;
import java.util.Set;

/**
 * Created by xs on 2017/4/17.
 */
public class AuthRealm extends AuthorizingRealm {
    @Resource
    private UserDao userDao;
    private static Logger log = LogUtil.getLogger(AuthRealm.class);
    /**
     * 为当前登录的Subject授予角色和权限
     *
     * 经测试:本例中该方法的调用时机为需授权资源被访问时
     * 经测试:并且每次访问需授权资源时都会执行该方法中的逻辑
     *
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        try {
            String username = (String) principals.getPrimaryPrincipal();
            if (Util.isBlank(username)) {
                log.info("获取权限异常！username 为空");
                return null;
            }
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            Set<String> roleIds = null;
            info.setRoles(roleIds);
            info.setStringPermissions(null);
            return info;
//        authorizationInfo.set
//        authorizationInfo.setRoles(userMng.findRoles(username));
        } catch (Exception e) {
            log.error("获取权限异常！");
            return null;
        }
    }
    /**
     * 验证当前登录的Subject
     *
     * 经测试:本例中该方法的调用时机为LoginController.login()方法中执行Subject.login()时
     *
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        try {
            String username = (String) token.getPrincipal();
            if (Util.isBlank(username)) {
                log.info("验证当前登录的Subject获取username异常！username 为空");
                return null;
            }
            User user = userDao.getUserByUsername(username);
            if (user == null) {
                log.info("没找到当前用户");
                return null;
            }
            log.info(username+";pwd=" + user.getPwd() + ";name=" + getName());
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(username,user.getPwd(),getName());
            return info;
        } catch (Exception e) {
            log.error("获取用户名密码异常！",e);
            return null;
        }
    }
}
