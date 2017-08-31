package xs.spider.work.bean;

import xs.spider.base.anno.Id;
import xs.spider.base.anno.Table;
import xs.spider.base.bean.BaseEntity;

/**
 * Created by xs on 2017/8/30
 */
@Table("mcc_customer")
@Id("id")
public class Customer extends BaseEntity {
    private Integer id;
    private String phone;
    private String share_code;
    private String channel_cust_id;
    private String channel_id;
    private String logout;

    public String getShare_code() {
        return share_code;
    }

    public void setShare_code(String share_code) {
        this.share_code = share_code;
    }

    public String getChannel_cust_id() {
        return channel_cust_id;
    }

    public void setChannel_cust_id(String channel_cust_id) {
        this.channel_cust_id = channel_cust_id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getLogout() {
        return logout;
    }

    public void setLogout(String logout) {
        this.logout = logout;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
