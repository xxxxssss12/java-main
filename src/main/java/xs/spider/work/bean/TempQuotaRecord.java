package xs.spider.work.bean;

import java.io.Serializable;

/**
 * Created by xs on 2017/9/27
 */
public class TempQuotaRecord implements Serializable {
    private String expireTime;
    private String finishTime;
    private Integer id;
    private Double payMoney;
    private Integer payStatus;
    private Integer showValue;
    private Integer validDay;

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Double payMoney) {
        this.payMoney = payMoney;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Integer getShowValue() {
        return showValue;
    }

    public void setShowValue(Integer showValue) {
        this.showValue = showValue;
    }

    public Integer getValidDay() {
        return validDay;
    }

    public void setValidDay(Integer validDay) {
        this.validDay = validDay;
    }
}
