package xs.spider.work.shiro.session.redis;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.slf4j.Logger;
import xs.spider.base.util.LogUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xs on 2018/10/23
 */
public class RedisSession implements ValidatingSession {
    private static Logger log = LogUtil.getLogger(RedisSession.class);
    private Serializable id;
    private Date startTimestamp;
    private Date lastAccessTime;
    private long timeout = 0L;
    private String host;
    private Map<Object, Object> attibutes = new HashMap<>();

    @Override
    public Serializable getId() {
        return id;
    }

    @Override
    public Date getStartTimestamp() {
        return startTimestamp;
    }

    @Override
    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    @Override
    public long getTimeout() throws InvalidSessionException {
        return timeout;
    }

    @Override
    public void setTimeout(long timeout) throws InvalidSessionException {
        this.timeout = timeout;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void touch() throws InvalidSessionException {
        this.lastAccessTime = new Date();
    }

    @Override
    public void stop() throws InvalidSessionException {
        this.timeout = 1L;
    }

    @JSONField(serialize=false)
    @Override
    public Collection<Object> getAttributeKeys() throws InvalidSessionException {
        if (attibutes != null) {
            return attibutes.keySet();
        }
        return null;
    }

    @Override
    public Object getAttribute(Object key) throws InvalidSessionException {
        return attibutes.get(key);
    }

    @Override
    public void setAttribute(Object key, Object value) throws InvalidSessionException {
        if (value == null) {
            attibutes.remove(key);
        } else {
            attibutes.put(key, value);
        }
    }

    @Override
    public Object removeAttribute(Object key) throws InvalidSessionException {
        return attibutes.remove(key);
    }

    public void setStartTimestamp(Date startTimestamp) {
        this.startTimestamp = startTimestamp;
        if (this.lastAccessTime == null) {
            this.lastAccessTime = startTimestamp;
        }
    }

    public void setId(Serializable id) {
        this.id = id;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setAttibutes(Map<Object, Object> attibutes) {
        this.attibutes = attibutes;
    }

    @Override
    public boolean isValid() {
        if (timeout <= 0) {
            log.debug("isValid-id={},isValid={}", id, true);
            return true;
        }
        if (lastAccessTime == null) {
            log.debug("isValid-id={},isValid={}", id, false);
            return false;
        }
        if (System.currentTimeMillis() - lastAccessTime.getTime() > timeout) {
            log.debug("isValid-id={},isValid={}", id, false);
            return false;
        }
        log.debug("isValid-id={},isValid={}", id, true);
        return true;
    }

    @Override
    public void validate() throws InvalidSessionException {
        log.info("validate-id={}", id);
    }
}
