package xs.spider.base.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import xs.spider.base.util.MD5Util;

/**
 * Created by xs on 2017/4/17.
 */
public class UserCredentialsMatcher  extends SimpleCredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info) {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        //密码加密保存，在此处将token.getPassword加密  以保证与数据库中加密后的密码匹配
        Object tokenCredentials = MD5Util.MD5(String.valueOf(token.getPassword()));
        Object accountCredentials = getCredentials(info);
        return equals(tokenCredentials, accountCredentials);
    }
}
