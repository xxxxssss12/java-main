package xs.spider.work.shiro.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 发现用户退出后，Session没有从Redis中销毁，虽然当前重新new了一个，但会对统计带来干扰，通过SessionListener解决这个问题
 * Created by xs on 2017/10/29
 */
public class RedisSessionListener implements SessionListener {

    @Autowired
    private RedisSessionDao sessionDao;
    @Override
    public void onStart(Session session) {

    }

    @Override
    public void onStop(Session session) {
        // 会话被停止时触发
        sessionDao.delete(session);
    }

    @Override
    public void onExpiration(Session session) {
        //会话过期时触发
        sessionDao.delete(session);
    }
}
