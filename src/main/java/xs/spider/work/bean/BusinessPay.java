package xs.spider.work.bean;

import xs.spider.base.anno.Id;
import xs.spider.base.anno.Table;
import xs.spider.base.bean.BaseEntity;

import java.util.Date;

/**
 * Created by xs on 2017/10/17
 */
@Id("id")
@Table("mcc_business_pay")
public class BusinessPay extends BaseEntity {
    private Integer id;
    private Integer customer_id;
    private String business_type;
    private String foreign_key;
    private Integer valid_day;
    private Date expire_time;
    private Double need_pay_money;
    private Integer pay_status;
    private String sequence_num;
    private String note;
    private Date create_time;
    private Date last_update_time;
    private Date finish_time;
    private String extract_value;
    private Integer is_valid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Integer customer_id) {
        this.customer_id = customer_id;
    }

    public String getBusiness_type() {
        return business_type;
    }

    public void setBusiness_type(String business_type) {
        this.business_type = business_type;
    }

    public String getForeign_key() {
        return foreign_key;
    }

    public void setForeign_key(String foreign_key) {
        this.foreign_key = foreign_key;
    }

    public Integer getValid_day() {
        return valid_day;
    }

    public void setValid_day(Integer valid_day) {
        this.valid_day = valid_day;
    }

    public Date getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(Date expire_time) {
        this.expire_time = expire_time;
    }

    public Double getNeed_pay_money() {
        return need_pay_money;
    }

    public void setNeed_pay_money(Double need_pay_money) {
        this.need_pay_money = need_pay_money;
    }

    public Integer getPay_status() {
        return pay_status;
    }

    public void setPay_status(Integer pay_status) {
        this.pay_status = pay_status;
    }

    public String getSequence_num() {
        return sequence_num;
    }

    public void setSequence_num(String sequence_num) {
        this.sequence_num = sequence_num;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getLast_update_time() {
        return last_update_time;
    }

    public void setLast_update_time(Date last_update_time) {
        this.last_update_time = last_update_time;
    }

    public Date getFinish_time() {
        return finish_time;
    }

    public void setFinish_time(Date finish_time) {
        this.finish_time = finish_time;
    }

    public String getExtract_value() {
        return extract_value;
    }

    public void setExtract_value(String extract_value) {
        this.extract_value = extract_value;
    }

    public Integer getIs_valid() {
        return is_valid;
    }

    public void setIs_valid(Integer is_valid) {
        this.is_valid = is_valid;
    }
}
