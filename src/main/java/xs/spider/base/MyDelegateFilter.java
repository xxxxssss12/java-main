package xs.spider.base;

import org.springframework.web.filter.DelegatingFilterProxy;
import xs.spider.base.util.LogUtil;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

/**
 * Created by xs on 2018/10/23
 */
public class MyDelegateFilter extends DelegatingFilterProxy {
    @Override
    protected void initFilterBean() throws ServletException {
        try {
            super.initFilterBean();
        } catch (Exception e) {
            LogUtil.error(getClass(), e);
            throw e;
        }
    }
}
