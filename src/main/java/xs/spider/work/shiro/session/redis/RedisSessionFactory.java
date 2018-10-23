package xs.spider.work.shiro.session.redis;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;
/**
 * Created by xs on 2017/10/29
 */
public class RedisSessionFactory implements SessionFactory {

    @Override
    public Session createSession(SessionContext initData) {
        RedisSession session = new RedisSession();
        return session;
    }
}
