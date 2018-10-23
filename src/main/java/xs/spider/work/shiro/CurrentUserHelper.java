package xs.spider.work.shiro;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.Subject;
import xs.spider.base.util.ApplicationContextHandle;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.RedisConnector;
import xs.spider.base.util.Util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by xs on 2017/10/12
 */
public class CurrentUserHelper {
    private static Logger log = LogUtil.getLogger(CurrentUserHelper.class, "permission");
    private static UserRealm userRealm;
    public static String cachePrefix = "shiro:auth:";//shiro缓存前缀
    public static String cacheRoleSuffix = ":roles";
    public static String cachePermissionSuffix = ":permission";
    public static String getCurrentUsername() {
        try {
            return Util.null2string(SecurityUtils.getSubject().getPrincipal());
        } catch (Exception e) {
            log.error("getCurrentUsername Error", e);
            return null;
        }
    }
    public static UserRealm getUserRealm() {
        if (userRealm == null) {
            userRealm = ApplicationContextHandle.getBean(UserRealm.class);
        }
        return userRealm;
    }

    /**
     * 是否已登录
     * @return
     */
    public static boolean isLogin() {
        try {
            Subject subject = SecurityUtils.getSubject();
            return subject.isAuthenticated();
        } catch (Exception e) {
            log.error("isLogin error!",e);
            return false;
        }
    }
    /**
     * 获取当前用户角色set
     * @return
     */
    public static Set<String> getCurrentUserRoles() {
        try {
//            Set<String> roleSet = getCacheRole();
            Set<String> roleSet = null;
            if (roleSet == null || roleSet.isEmpty()) {
                AuthorizationInfo info = getUserRealm().doGetAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
                roleSet = (Set<String>) info.getStringPermissions();
//                setCacheRole(roleSet);
                return (Set<String>) info.getRoles();
            }
        } catch(Exception e) {
            log.error("getCurrentUserRoles Error", e);
        }
        return null;
    }
    /**
     * 获取当前用户权限项set
     * @return
     */
    public static Set<String> getCurrentUserPermissions() {
        try {
//            Set<String> perSet = getCachePermission();
            Set<String> perSet = null;
            if (perSet == null || perSet.isEmpty()) {
                AuthorizationInfo info = getUserRealm().doGetAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
                perSet = (Set<String>) info.getStringPermissions();
//                setCachePerm(perSet);
            }
            return perSet;
        } catch (Exception e) {
            log.error("getCurrentUserPermissions Error", e);
            return null;
        }
    }
    private static void setCacheRole(Set<String> roles) {
        if (roles!=null && !roles.isEmpty()) {
            RedisConnector.set(cachePrefix + getCurrentUsername() + cacheRoleSuffix, JSON.toJSONString(roles));
        }
    }
    private static void setCachePerm(Set<String> perms) {
        if (perms != null && !perms.isEmpty()) {
            RedisConnector.set(cachePrefix + getCurrentUsername() + cachePermissionSuffix, JSON.toJSONString(perms));
        }
    }
    private static Set<String> getCacheRole() {
        String username = getCurrentUsername();
        if (!Util.isBlank(username)) {
            String roleJson = RedisConnector.get(cachePrefix + username + cacheRoleSuffix);
            if (!Util.isBlank(roleJson)) {
                try {
                    List<String> roleList = JSON.parseArray(roleJson, String.class);
                    if (roleList != null && !roleList.isEmpty()) {
                        Set<String> roleSet = new HashSet<>();
                        roleSet.addAll(roleList);
                        return roleSet;
                    }
                } catch (Exception e) {
                    log.error("getCacheRole Error!",e);
                }
            }
        }
        return null;
    }
    private static Set<String> getCachePermission() {
        String username = getCurrentUsername();
        if (!Util.isBlank(username)) {
            String perJson = RedisConnector.get(cachePrefix + username + cachePermissionSuffix);
            if (!Util.isBlank(perJson)) {
                try {
                    List<String> perList = JSON.parseArray(perJson, String.class);
                    if (perList != null && !perList.isEmpty()) {
                        Set<String> perSet = new HashSet<>();
                        perSet.addAll(perList);
                        return perSet;
                    }
                } catch (Exception e) {
                    log.error("getCachePermission Error!",e);
                }
            }
        }
        return null;
    }
}
