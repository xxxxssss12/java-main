package xs.spider.work.bean;

import xs.spider.base.anno.Id;
import xs.spider.base.anno.Table;
import xs.spider.base.bean.BaseEntity;

import java.util.Date;

/**
 * Created by xs on 2017/11/12
 */
@Table("tb_model_borrow_record")
@Id("id")
public class ModelBorrowRecord extends BaseEntity {
    private Integer id;
    private Integer modelAllId;
    private String borrowCompany;
    private String approvalPerson;
    private String managePerson;
    private String borrowCustomer;
    private String preLocation;
    private Date borrowTime;
    private Date returnTime;
    private Integer isReturn;
    private String remark;
    private String createUser;
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getModelAllId() {
        return modelAllId;
    }

    public void setModelAllId(Integer modelAllId) {
        this.modelAllId = modelAllId;
    }

    public String getBorrowCompany() {
        return borrowCompany;
    }

    public void setBorrowCompany(String borrowCompany) {
        this.borrowCompany = borrowCompany;
    }

    public String getApprovalPerson() {
        return approvalPerson;
    }

    public void setApprovalPerson(String approvalPerson) {
        this.approvalPerson = approvalPerson;
    }

    public String getManagePerson() {
        return managePerson;
    }

    public void setManagePerson(String managePerson) {
        this.managePerson = managePerson;
    }

    public String getBorrowCustomer() {
        return borrowCustomer;
    }

    public void setBorrowCustomer(String borrowCustomer) {
        this.borrowCustomer = borrowCustomer;
    }

    public Date getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(Date borrowTime) {
        this.borrowTime = borrowTime;
    }

    public Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
    }

    public Integer getIsReturn() {
        return isReturn;
    }

    public void setIsReturn(Integer isReturn) {
        this.isReturn = isReturn;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getPreLocation() {
        return preLocation;
    }

    public void setPreLocation(String preLocation) {
        this.preLocation = preLocation;
    }
}
