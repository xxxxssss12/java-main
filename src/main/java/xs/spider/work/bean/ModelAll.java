package xs.spider.work.bean;

import xs.spider.base.anno.Id;
import xs.spider.base.anno.Table;
import xs.spider.base.bean.BaseEntity;

import java.util.Date;

/**
 * 模具总表
 * Created by xs on 2017/8/7.
 */
@Id("id")
@Table("tb_model_all")
public class ModelAll extends BaseEntity {
    private Integer id;
    private String modelNo;  // 图纸编号
    private String name;        // 名称
    private String lingjiancaizhi;      // 组块
    private Date createTime;   // 制造时间
    private String createLocation;     // 制造位置
    private String storeLocation;      // 存放位置
    private Integer functionTypeId;   // 功能类型表主键
    private Integer useLevelId;       // 使用级别表id（民用，军工级，核电级）
    private String materialQuality;    // 铸件材质
    private String modelTypeName;
    private Integer modelTypeId;
    private Integer isUnuse;
    private String remark;      // 备注
    private Integer isDel;
    private Date addtime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getModelNo() {
        return modelNo;
    }

    public void setModelNo(String modelNo) {
        this.modelNo = modelNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLingjiancaizhi() {
        return lingjiancaizhi;
    }

    public void setLingjiancaizhi(String lingjiancaizhi) {
        this.lingjiancaizhi = lingjiancaizhi;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateLocation() {
        return createLocation;
    }

    public void setCreateLocation(String createLocation) {
        this.createLocation = createLocation;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
    }

    public Integer getFunctionTypeId() {
        return functionTypeId;
    }

    public void setFunctionTypeId(Integer functionTypeId) {
        this.functionTypeId = functionTypeId;
    }

    public Integer getUseLevelId() {
        return useLevelId;
    }

    public void setUseLevelId(Integer useLevelId) {
        this.useLevelId = useLevelId;
    }

    public String getMaterialQuality() {
        return materialQuality;
    }

    public void setMaterialQuality(String materialQuality) {
        this.materialQuality = materialQuality;
    }

    public Integer getModelTypeId() {
        return modelTypeId;
    }

    public void setModelTypeId(Integer modelTypeId) {
        this.modelTypeId = modelTypeId;
    }

    public Integer getIsUnuse() {
        return isUnuse;
    }

    public void setIsUnuse(Integer isUnuse) {
        this.isUnuse = isUnuse;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getModelTypeName() {
        return modelTypeName;
    }

    public void setModelTypeName(String modelTypeName) {
        this.modelTypeName = modelTypeName;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }
}
