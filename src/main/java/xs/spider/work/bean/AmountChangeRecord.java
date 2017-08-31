package xs.spider.work.bean;

import xs.spider.base.anno.Id;
import xs.spider.base.anno.Table;
import xs.spider.base.bean.BaseEntity;

import java.util.Date;

/**
 * Created by xs on 2017/8/30
 */
@Table("mcc_amount_change_record")
@Id("id")
public class AmountChangeRecord extends BaseEntity {
    private Integer id;
    private Double amount_change;
    private Integer customer_id;
    private Date biz_grant_time;
    private Double amount_before;
    private Double amount_after;

    public Date getBiz_grant_time() {
        return biz_grant_time;
    }

    public void setBiz_grant_time(Date biz_grant_time) {
        this.biz_grant_time = biz_grant_time;
    }

    public Double getAmount_before() {
        return amount_before;
    }

    public void setAmount_before(Double amount_before) {
        this.amount_before = amount_before;
    }

    public Double getAmount_after() {
        return amount_after;
    }

    public void setAmount_after(Double amount_after) {
        this.amount_after = amount_after;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getAmount_change() {
        return amount_change;
    }

    public void setAmount_change(Double amount_change) {
        this.amount_change = amount_change;
    }

    public Integer getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Integer customer_id) {
        this.customer_id = customer_id;
    }
}
