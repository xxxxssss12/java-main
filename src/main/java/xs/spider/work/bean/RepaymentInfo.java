package xs.spider.work.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by xs on 2017/8/30
 */
public class RepaymentInfo implements Serializable {
    private Integer investor_id;
    private String should_date;
    private Integer overdue_day;
    private Date end_date;
    private Integer repayment_status;
    private Double overdue_amount;
    private Double should_amount;
    private Integer customer_id;

    public Integer getInvestor_id() {
        return investor_id;
    }

    public void setInvestor_id(Integer investor_id) {
        this.investor_id = investor_id;
    }

    public String getShould_date() {
        return should_date;
    }

    public void setShould_date(String should_date) {
        this.should_date = should_date;
    }

    public Integer getOverdue_day() {
        return overdue_day;
    }

    public void setOverdue_day(Integer overdue_day) {
        this.overdue_day = overdue_day;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public Integer getRepayment_status() {
        return repayment_status;
    }

    public void setRepayment_status(Integer repayment_status) {
        this.repayment_status = repayment_status;
    }

    public Double getOverdue_amount() {
        return overdue_amount;
    }

    public void setOverdue_amount(Double overdue_amount) {
        this.overdue_amount = overdue_amount;
    }

    public Double getShould_amount() {
        return should_amount;
    }

    public void setShould_amount(Double should_amount) {
        this.should_amount = should_amount;
    }

    public Integer getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Integer customer_id) {
        this.customer_id = customer_id;
    }
}
