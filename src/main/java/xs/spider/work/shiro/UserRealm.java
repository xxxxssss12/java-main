package xs.spider.work.shiro;

import com.google.common.collect.Sets;
import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.Util;
import xs.spider.work.bean.User;
import xs.spider.work.service.UserServiceImpl;

import java.util.Set;

/**
 * Created by xs on 2017/10/6.
 */
@Component
public class UserRealm extends AuthorizingRealm {
    private static Logger log = LogUtil.getLogger(UserRealm.class, "permission");
    @Autowired
    private UserServiceImpl userService;
    /**
     * 为当前登录的Subject授予角色和权限
     *
     * @see 经测试:本例中该方法的调用时机为需授权资源被访问时
     * @see 经测试:并且每次访问需授权资源时都会执行该方法中的逻辑
     *
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        try {
            String username = (String) principals.getPrimaryPrincipal();
            User user = userService.getUserByUsername(username);
            if (Util.isBlank(user)) {
                log.error("获取权限异常！user 为空");
                return null;
            }
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            Set<String> roleIds = Sets.newHashSet();
            info.setRoles(roleIds);
            info.setStringPermissions(userService.getPermission(user));
            return info;
        } catch (Exception e) {
            log.error("获取权限异常！");
            return null;
        }
    }
    /**
     * 验证当前登录的Subject
     *
     * @see 经测试:本例中该方法的调用时机为LoginController.login()方法中执行Subject.login()时
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
            User user = userService.getUserByUsername(username);
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(username,user.getPassword(),getName());
            return info;
        } catch (Exception e) {
            log.error("获取用户名密码异常！",e);
            return null;
        }
    }
}
