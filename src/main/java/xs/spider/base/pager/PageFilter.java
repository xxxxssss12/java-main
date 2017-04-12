package xs.spider.base.pager;

import xs.spider.base.util.Util;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by hasee on 2016/12/24.
 */
public class PageFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        PageContext.setPageNum(getPageNum(req));
        PageContext.setPageSize(getPageSize(req));
        try {
            chain.doFilter(request, response);
        }
        //使用完Threadlocal，将其删除。使用finally确保一定将其删除
        finally{
            PageContext.removePageNum();
            PageContext.removePageSize();
        }
    }
    /**
     * 获得pager.offset参数的值
     * @param request
     * @return
     */
    protected int getPageNum(HttpServletRequest request) {
        int pageNum = 10;
        try {
            String pageOff = request.getParameter("pager.pageNum");
            if (!Util.isBlank(pageOff)) {
                pageNum =Integer.parseInt(pageOff);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return pageNum;
    }
    /**
     * 设置默认每页大小
     * @return
     */
    protected int getPageSize(HttpServletRequest request) {
        int pageSize = 0;
        try {
            String pageOff = request.getParameter("pager.pageSize");
            if (!Util.isBlank(pageOff)) {
                pageSize =Integer.parseInt(pageOff);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return pageSize;
    }
    public void destroy() {

    }
}
