package xs.spider.work.shiro.session.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.slf4j.Logger;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.RedisConnector;
import xs.spider.base.util.Util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by xs on 2018/10/23
 */
public class RedisSessionDao extends AbstractSessionDAO {
    private static Logger log = LogUtil.getLogger(RedisSessionDao.class);
    private static String session_map_key = "xs:shiro:session:map";
    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        ((RedisSession)session).setId(sessionId);
        String json = toJsonString(session);
        log.debug("doCreate-class={}, val={}, trace={}", session.getClass().getName(), json);
        RedisConnector.hset(session_map_key, "session:" + sessionId, json);
        return sessionId;
    }
    @Override
    protected Session doReadSession(Serializable sessionId) {
        String json = RedisConnector.hget(session_map_key, "session:" + sessionId);
        log.debug("doReadSession-class={}, id={}, val={}, trace={}", sessionId.getClass().getName(), json, sessionId);
        if (json != null) {
            return (Session) parseObject(json);
        }
        return null;
    }

    public static void main(String[] args) {
        RedisSession redisSession = new RedisSession();
        redisSession.setId("id");
        redisSession.setTimeout(180000);
        Map<String, Object> map = new HashMap<>();
        map.put("1", 112);
        System.out.println(JSON.toJSONString(redisSession, profilter));
    }
    @Override
    public void update(Session session) throws UnknownSessionException {
        Serializable id = session.getId();
        log.debug("update-class={}, val={}, trace={}", session.getClass().getName(), JSON.toJSONString(session, profilter));
        String json = RedisConnector.hget(session_map_key, "session:" + id);
        if (Util.isBlank(json)) {
            RedisConnector.hset(session_map_key, "session:" + id, toJsonString(session));
        }
    }

    @Override
    public void delete(Session session) {
        log.debug("delete-class={}, val={}, trace={}", session.getClass().getName(), JSON.toJSONString(session, profilter));
        if (session.getId() != null) {
            RedisConnector.hdel(session_map_key, "session:" + session.getId());
        }
    }

    @Override
    public Collection<Session> getActiveSessions() {
        log.debug("getActiveSessions(),stackInfo={}");
        Map<String, String> map = RedisConnector.hget(session_map_key);
        Collection<Session> values = new HashSet<>();
        if (map != null && !map.isEmpty()) {
            for (String json : map.keySet()) {
                values.add((Session) parseObject(json));
            }
        }
        return values;
    }

    private String toJsonString(Object session) {
        String json = JSON.toJSONString(session, profilter);
        JSONObject jsonObj = JSON.parseObject(json);
        jsonObj.put("__class__", session.getClass().getName());
        return jsonObj.toJSONString();
    }

    private Object parseObject(String json) {
        try {
            JSONObject jsonObj = JSON.parseObject(json);
            String className = jsonObj.getString("__class__");
            jsonObj.remove("__class__");
            return JSON.parseObject(jsonObj.toJSONString(), Class.forName(className));
        } catch (Exception e) {
            log.error("parseObject error!", e);
            return null;
        }
    }

    private static PropertyFilter profilter = new PropertyFilter() {
            @Override
            public boolean apply(Object object, String name, Object value) {
                if(name.equalsIgnoreCase("attributeKeys")
                        || name.equalsIgnoreCase("valid")) {
                    //false表示last字段将被排除在外  
                    return false;
                }
                return true;
            }

    };
}
