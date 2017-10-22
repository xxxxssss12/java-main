package xs.spider.work.shiro;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.config.ConfigProvider;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by xs on 2017/10/12
 */
public class AuthUtil {
    public static String commonUrl = ConfigProvider.get("auth.common.url");
    public static String isOpen = ConfigProvider.get("auth.open");
    public static List<String> nocheckUrls;
    public static String loginUrl = ConfigProvider.get("auth.login.url");

    static {
        String nocheckUrl = ConfigProvider.get("auth.nocheck.url");
        if (!Util.isBlank(nocheckUrl)) nocheckUrls = Arrays.asList(nocheckUrl.split(","));
    }
    private static Logger log = LogUtil.getLogger(AuthUtil.class, "permission");

    public static ResultInfo buildResp(int code) {
        switch (code) {
            case 1:
                //成功
                return ResultInfo.build();
            case -1:
                //url为空
                return ResultInfo.build(-999,"请求的url为空");
            case -2:
                //登录超时
                return ResultInfo.build(-998,"请重新登录");
            case -3:
                //权限校验失败
                return ResultInfo.build(-997,"您没有操作权限");
            default:
                return ResultInfo.build(-996, "未知错误");
        }
    }

}
