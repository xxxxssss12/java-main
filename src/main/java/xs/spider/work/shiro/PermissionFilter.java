package xs.spider.work.shiro;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.util.ExceptionWrite;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.Util;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Set;

/**
 * Created by xs on 2017/10/12
 */
public class PermissionFilter implements Filter {
    private static Logger log = LogUtil.getLogger(PermissionFilter.class, "permission");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        OutputStream out = servletResponse.getOutputStream();
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            if (!Util.isBlank(AuthUtil.isOpen) && AuthUtil.isOpen.equals("true")) {
                log.info("权限isopen=true，全部放行");
                chain.doFilter(servletRequest,servletResponse);
                return;
            }
            HttpServletResponse response = (HttpServletResponse) servletResponse;
//            String url = Util.null2string(request.getRequestURL());
            String url = request.getServletPath().substring(1);
            log.info(Util.getIpAddr(request) + ":" + CurrentUserHelper.getCurrentUsername() + "请求进入：" + url);

            if (CurrentUserHelper.isLogin() && !Util.isBlank(CurrentUserHelper.getCurrentUsername())) {
//                if (url.indexOf(AuthUtil.commonUrl) != -1) {
//                    url = url.replaceAll(AuthUtil.commonUrl, "");
                if (AuthUtil.nocheckUrls.contains(url)) {
                    //放行
                    chain.doFilter(servletRequest,servletResponse);
                    return;
                } else {
                    ResultInfo authRes = checkUrl(url);
                    if (1 == authRes.getCode()) {
                        //鉴权通过
                        chain.doFilter(servletRequest,servletResponse);
                        return;
                    } else {
                        servletResponse.setContentType("application/json;charset=UTF-8");
                        out.write(JSON.toJSONString(authRes).getBytes("utf-8"));
                        return;
                    }
                }
//                }
//                chain.doFilter(servletRequest, servletResponse);
            } else {
                log.info("用户:" + CurrentUserHelper.getCurrentUsername() + "..登录超时");
                response.setHeader("session-timeout", "true");
                response.setStatus(403);
                response.setContentType("application/json;charset=UTF-8");
                out.write(JSON.toJSONString(ResultInfo.buildFail("登陆超时")).getBytes("utf-8"));
//                response.sendRedirect(AuthUtil.commonUrl + AuthUtil.loginUrl + "?redirect=" + URLEncoder.encode(url, "utf-8"));
                return;
            }
        } catch (Exception e) {
            log.error("系统异常:" + ExceptionWrite.get(e));
            servletResponse.setContentType("application/json;charset=UTF-8");
            out.write(JSON.toJSONString(ResultInfo.buildFail("系统异常")).getBytes("utf-8"));
        }
    }

    public ResultInfo checkUrl(String url) {
        int code =0;
        if (Util.isBlank(url)) {
            code= -1;
        }
        Subject subject = SecurityUtils.getSubject();
        if (subject == null) {
            log.info("尚未登录");
            code= -2;
        }
        try {
            return AuthUtil.buildResp(checkPermission(subject, url));
//            subject.checkPermission(url);
        } catch (UnauthenticatedException e) {
            log.info("鉴权失败(登陆超时？)："+e.getMessage());
            code= -2;
        } catch (UnauthorizedException e) {
            log.info("鉴权失败(用户没有权限访问该接口):"+e.getMessage());
            code= -3;
        }
        return AuthUtil.buildResp(code);
    }

    public int checkPermission(Subject subject, String url) {
        Set<String> permissions = CurrentUserHelper.getCurrentUserPermissions();
        if (permissions == null) return -3;
        for (String permission : permissions) {
            if (!Util.isBlank(permission) && url.toLowerCase().contains(permission.toLowerCase())) {
                log.info("用户有权限：permission=" + permission + ";url=" + url);
                return 1;
            }
        }
        return -3;
    }
    @Override
    public void destroy() {

    }
}
