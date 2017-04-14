package xs.spider.work.bean;

import xs.spider.base.anno.Table;
import xs.spider.base.bean.BaseEntity;

import java.util.Date;

/**
 * Created by xs on 2017/4/5.
 */
@Table("title_info")
public class TitleInfo extends BaseEntity {
    private Integer id;
    private String url;
    private String content;
    private Date time;
    private Integer pagenum;
    private Integer isValid;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getPagenum() {
        return pagenum;
    }

    public void setPagenum(Integer pagenum) {
        this.pagenum = pagenum;
    }

    @Override
    public String getPkName() {
        return "id";
    }

    public Integer getIsValid() {
        return isValid;
    }

    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }
}
