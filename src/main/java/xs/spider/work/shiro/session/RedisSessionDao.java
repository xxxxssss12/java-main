package xs.spider.work.shiro.session;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.RedisConnector;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.List;

/**
 * Created by xs on 2017/10/29
 */
public class RedisSessionDao extends CachingSessionDAO {
    private static final Logger logger = LogUtil.getLogger(RedisSessionDao.class);

    // 保存到Redis中key的前缀 prefix+sessionId
    private String prefix = "shiro:session:";

    // 设置会话的过期时间
    private int seconds = 0;

    /**
     * 重写CachingSessionDAO中readSession方法，如果Session中没有登陆信息就调用doReadSession方法从Redis中重读
     */
    @Override
    public Session readSession(Serializable sessionId) throws UnknownSessionException {
        Session session = getCachedSession(sessionId);
        if (session == null
                || session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY) == null) {
            session = this.doReadSession(sessionId);
            if (session == null) {
                throw new UnknownSessionException("There is no session with id [" + sessionId + "]");
            } else {
                // 缓存
                cache(session, session.getId());
            }
        }
        return session;
    }

    /**
     * 根据会话ID获取会话
     *
     * @param sessionId 会话ID
     * @return ShiroSession
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        Session session = null;
        try {
            String key = prefix + sessionId;
            String value = RedisConnector.get(key);
            if (StringUtils.isNotBlank(value)) {
                session = SerializeUtils.deserializeFromString(value);
                logger.debug("sessionId "+sessionId+" ttl " + RedisConnector.ttl(key) + ": ");
                // 重置Redis中缓存过期时间
                RedisConnector.expire(key, seconds);
                logger.debug("sessionId "+sessionId+" name " + session.getClass().getName() + " 被读取");
            }
        } catch (Exception e) {
            logger.warn("读取Session失败", e);
        }
        return session;
    }

    public Session doReadSessionWithoutExpire(Serializable sessionId) {
        Session session = null;
        try {
            String key = prefix + sessionId;
            String value = RedisConnector.get(key);
            if (StringUtils.isNotBlank(value)) {
                session = SerializeUtils.deserializeFromString(value);
            }
        } catch (Exception e) {
            logger.warn("读取Session失败", e);
        }
        return session;
    }

    /**
     * 如DefaultSessionManager在创建完session后会调用该方法；
     * 如保存到关系数据库/文件系统/NoSQL数据库；即可以实现会话的持久化；
     * 返回会话ID；主要此处返回的ID.equals(session.getId())；
     */
    @Override
    protected Serializable doCreate(Session session) {
        // 创建一个Id并设置给Session
        Serializable sessionId = this.generateSessionId(session);
        assignSessionId(session, sessionId);
        try {
            // session由Redis缓存失效决定，这里只是简单标识
            session.setTimeout(seconds);
            RedisConnector.setex(prefix + sessionId, seconds, SerializeUtils.serializeToString((RedisSession) session));
            logger.info("sessionId " + sessionId + " name " + session.getClass().getName() + " 被创建");
        } catch (Exception e) {
            logger.warn("创建Session失败", e);
        }
        return sessionId;
    }

    /**
     * 更新会话；如更新会话最后访问时间/停止会话/设置超时时间/设置移除属性等会调用
     */
    @Override
    protected void doUpdate(Session session) {
        //如果会话过期/停止 没必要再更新了
        try {
            if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid()) {
                return;
            }
        } catch (Exception e) {
            logger.error("ValidatingSession error");
        }
        try {
            if (session instanceof RedisSession) {
                // 如果没有主要字段(除lastAccessTime以外其他字段)发生改变
                RedisSession ss = (RedisSession) session;
                if (!ss.isChanged()) {
                    return;
                }
                try {
                    ss.setChanged(false);
                    RedisConnector.setex(prefix + session.getId(), seconds, SerializeUtils.serializeToString(ss));
                    logger.debug("sessionId "+session.getId()+" name " + session.getClass().getName() + " 被更新");
                } catch (Exception e) {
                    throw e;
                }

            } else if (session instanceof Serializable) {
                RedisConnector.setex(prefix + session.getId(), seconds, SerializeUtils.serializeToString((Serializable) session));
                logger.debug("sessionId " + session.getId() + " name " + session.getClass().getName() + " 作为非ShiroSession对象被更新, ");
            } else {
                logger.warn("sessionId "+session.getId()+" name "+session.getClass().getName()+" 不能被序列化 更新失败");
            }
        } catch (Exception e) {
            logger.warn("更新Session失败", e);
        }
    }

    /**
     * 删除会话；当会话过期/会话停止（如用户退出时）会调用
     */
    @Override
    protected void doDelete(Session session) {
        try {
            RedisConnector.del(prefix + session.getId());
            logger.debug("Session "+session.getId()+" 被删除");
        } catch (Exception e) {
            logger.warn("修改Session失败", e);
        }
    }

    /**
     * 删除cache中缓存的Session
     */
    public void uncache(Serializable sessionId) {
        Session session = this.readSession(sessionId);
        super.uncache(session);
        logger.info("取消session "+sessionId+" 的缓存");
    }

    /**
     * 获取当前所有活跃用户，如果用户量多此方法影响性能
     */
    @Override
    public Collection<Session> getActiveSessions() {
        try {
            Set<String> keys = RedisConnector.keys(prefix + "*");
            if (CollectionUtils.isEmpty(keys)) {
                return null;
            }
            List<String> valueList = RedisConnector.getJedis().mget(keys.toArray(new String[0]));
            return SerializeUtils.deserializeFromStringController(valueList);
        } catch (Exception e) {
            logger.warn("统计Session信息失败", e);
        }
        return null;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

}
class SerializeUtils extends SerializationUtils {

    public static String serializeToString(Serializable obj) {
        try {
            byte[] value = serialize(obj);
            return Base64.encodeToString(value);
        } catch (Exception e) {
            throw new RuntimeException("serialize session error", e);
        }
    }

    public static Session deserializeFromString(String base64) {
        try {
            byte[] objectData = Base64.decode(base64);
            return deserialize(objectData);
        } catch (Exception e) {
            throw new RuntimeException("deserialize session error", e);
        }
    }

    public static <T> Collection<T> deserializeFromStringController(Collection<String> base64s) {
        try {
            List<T> list = Lists.newLinkedList();
            for (String base64 : base64s) {
                byte[] objectData = Base64.decode(base64);
                T t = deserialize(objectData);
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("deserialize session error", e);
        }
    }
}