package xs.spider.work.bean;

import xs.spider.base.anno.Id;
import xs.spider.base.anno.Table;
import xs.spider.base.bean.BaseEntity;

import java.util.Date;

/**
 * 磨具明细表
 * Created by xs on 2017/8/7.
 */
@Id("id")
@Table("tb_model_detail")
public class ModelDetail extends BaseEntity {
    private Integer id;
    private String detail;
    private String remark;
    private Integer modelAllId;
    private Integer num;
    private String partName;
    private Date createTime;
    private String createUser;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getModelAllId() {
        return modelAllId;
    }

    public void setModelAllId(Integer modelAllId) {
        this.modelAllId = modelAllId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
}
