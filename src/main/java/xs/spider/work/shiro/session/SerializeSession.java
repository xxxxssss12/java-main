package xs.spider.work.shiro.session;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by xs on 2017/10/29
 */
public class SerializeSession {
    private Serializable id;
    private Date startTimestamp;
    private Date stopTimestamp;
    private Date lastAccessTime;
    private long timeout;
    private boolean expired;
    private String host;
    private Map<Object, Object> attributes;
    public SerializeSession() {

    }
    public SerializeSession(Serializable id, Date startTimestamp, Date stopTimestamp, Date lastAccessTime, long timeout, boolean expired, String host, Map<Object, Object> attributes) {
        this.id = id;
        this.startTimestamp = startTimestamp;
        this.stopTimestamp = stopTimestamp;
        this.lastAccessTime = lastAccessTime;
        this.timeout = timeout;
        this.expired = expired;
        this.host = host;
        this.attributes = attributes;
    }

    public Serializable getId() {
        return id;
    }

    public void setId(Serializable id) {
        this.id = id;
    }

    public Date getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Date startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Date getStopTimestamp() {
        return stopTimestamp;
    }

    public void setStopTimestamp(Date stopTimestamp) {
        this.stopTimestamp = stopTimestamp;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Map<Object, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<Object, Object> attributes) {
        this.attributes = attributes;
    }
}
