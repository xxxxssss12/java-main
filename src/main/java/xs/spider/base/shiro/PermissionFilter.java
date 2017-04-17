package xs.spider.base.shiro;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.config.ConfigProvider;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.PropUtil;
import xs.spider.base.util.Util;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by xs on 2017/4/17.
 */
public class PermissionFilter implements Filter {
    private static Logger log = LogUtil.getLogger(PermissionFilter.class);
    private static Set<String> noAuthUrls;
    static {
        String str = ConfigProvider.get("noauth.urls","");
        noAuthUrls = new HashSet<String>(Arrays.asList(str.split(",")));
    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
    private String projectName = "spider_douban/";
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        OutputStream out = servletResponse.getOutputStream();
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            servletResponse.setCharacterEncoding("UTF-8");
            String url = Util.null2string(request.getRequestURL());
            log.info(Util.null2string(SecurityUtils.getSubject().getPrincipal()) + "请求进入：" + url);
            String[] temparr = url.split(projectName);
            if (temparr != null && temparr.length > 1) {
                url = temparr[1];
            } else {
                //放行
                chain.doFilter(servletRequest,servletResponse);
                return;
            }
            if (url.indexOf(".") != -1 || noAuthUrls.contains(url)) {
                //放行
                chain.doFilter(servletRequest,servletResponse);
                return;
            }
            if (isLogin() && !Util.isBlank(Util.null2string(SecurityUtils.getSubject().getPrincipal()))) {
                chain.doFilter(servletRequest, servletResponse);
                return;
            } else {
                log.info("用户:" + Util.null2string(SecurityUtils.getSubject().getPrincipal()) + " 登录超时");
                out.write(JSON.toJSONBytes(new ResultInfo(-1,"请重新登陆")));
                return;
            }
            /*
            if (url.indexOf(AuthUtil.commonUrl) != -1) {
                url = url.replaceAll(AuthUtil.commonUrl, "" );
                if (AuthUtil.nocheckUrls.contains(url)) {
                    //放行
                    chain.doFilter(servletRequest,servletResponse);
                } else {
                   RespBody authRes = AuthUtil.checkUrl(url);
                   if ("1".equals(authRes.getSuccessful())) {
                       //鉴权通过
                       chain.doFilter(servletRequest,servletResponse);
                       return;
                   } else {
                       out.write(JSON.toJSONBytes(authRes));
                   }
                }
            } else {
                log.info(AuthUtil.commonUrl);
                out.write(JSON.toJSONBytes(RespBody.buildFail("未知错误")));
            }
            */
        } catch (Exception e) {
            log.error("鉴权filter异常",e);
            out.write(JSON.toJSONBytes(new ResultInfo(-1,"系统异常")));
        }
    }

    @Override
    public void destroy() {

    }
    public static boolean isLogin() {
        try {
            Subject subject = SecurityUtils.getSubject();
            return subject.isAuthenticated();
        } catch (Exception e) {
            log.error("isLogin error!",e);
            return false;
        }
    }
}
