package xs.spider.work.bean;

import xs.spider.base.anno.Id;
import xs.spider.base.anno.InsertIgnore;
import xs.spider.base.anno.Table;
import xs.spider.base.bean.BaseEntity;

import java.util.Date;

/**
 * Created by xs on 2017/10/12
 */
@Table("tb_user")
@Id("id")
public class User extends BaseEntity {
    private Integer id;
    private String username;
    private String realName;
    private String password;
    private Date createTime;
    private Integer userLevel;
    private Integer roleId;
    private String phone;
    private String email;
    @InsertIgnore
    private String roleName;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
