package xs.spider.work.bean;

import xs.spider.base.anno.Id;
import xs.spider.base.anno.Table;
import xs.spider.base.bean.BaseEntity;

import java.util.Date;

/**
 * Created by xs on 2017/7/30
 */
@Table("come_record")
@Id("id")
public class ComeRecord extends BaseEntity {
    private Integer id;
    private String ip;
    private Date createTime;
    private Date lastUpTime;
    private Long stayTime;
    private Integer failTime;   // 失败次数
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpTime() {
        return lastUpTime;
    }

    public void setLastUpTime(Date lastUpTime) {
        this.lastUpTime = lastUpTime;
    }

    public Long getStayTime() {
        return stayTime;
    }

    public void setStayTime(Long stayTime) {
        this.stayTime = stayTime;
    }

    public Integer getFailTime() {
        return failTime;
    }

    public void setFailTime(Integer failTime) {
        this.failTime = failTime;
    }
}
