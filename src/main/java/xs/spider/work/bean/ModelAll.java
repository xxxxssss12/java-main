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
    private String drawing_no;  // 图纸编号
    private String name;        // 名称
    private String zukuai;      // 组块
    private String remark;      // 备注
    private Date create_time;   // 制造时间
    private String create_location;     // 制造位置
    private String store_location;      // 存放位置
    private Integer function_type_id;   // 功能类型表主键
    private Integer use_level_id;       // 使用级别表id（民用，军工级，核电级）
    private String material_quality;    // 铸件材质

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDrawing_no() {
        return drawing_no;
    }

    public void setDrawing_no(String drawing_no) {
        this.drawing_no = drawing_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZukuai() {
        return zukuai;
    }

    public void setZukuai(String zukuai) {
        this.zukuai = zukuai;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getCreate_location() {
        return create_location;
    }

    public void setCreate_location(String create_location) {
        this.create_location = create_location;
    }

    public String getStore_location() {
        return store_location;
    }

    public void setStore_location(String store_location) {
        this.store_location = store_location;
    }

    public Integer getFunction_type_id() {
        return function_type_id;
    }

    public void setFunction_type_id(Integer function_type_id) {
        this.function_type_id = function_type_id;
    }

    public Integer getUse_level_id() {
        return use_level_id;
    }

    public void setUse_level_id(Integer use_level_id) {
        this.use_level_id = use_level_id;
    }

    public String getMaterial_quality() {
        return material_quality;
    }

    public void setMaterial_quality(String material_quality) {
        this.material_quality = material_quality;
    }
}
