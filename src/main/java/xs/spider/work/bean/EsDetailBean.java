package xs.spider.work.bean;

import com.alibaba.fastjson.JSONArray;

/**
 * Created by xs on 2017/10/2.
 */
public class EsDetailBean {
    private Integer titleId;
    private Integer contentId;
    private String title;
    private String content;
    private JSONArray picInfo;
    private String time;
    private Integer isValid;
    private String titleUrl;
    private Long createTimestamp;
    private String highlights;
    private Integer isHavePic;
    public Integer getTitleId() {
        return titleId;
    }

    public void setTitleId(Integer titleId) {
        this.titleId = titleId;
    }

    public Integer getContentId() {
        return contentId;
    }

    public void setContentId(Integer contentId) {
        this.contentId = contentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public JSONArray getPicInfo() {
        return picInfo;
    }

    public void setPicInfo(JSONArray picInfo) {
        this.picInfo = picInfo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getIsValid() {
        return isValid;
    }

    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }

    public String getTitleUrl() {
        return titleUrl;
    }

    public void setTitleUrl(String titleUrl) {
        this.titleUrl = titleUrl;
    }

    public Long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public String getHighlights() {
        return highlights;
    }

    public void setHighlights(String highlights) {
        this.highlights = highlights;
    }

    public Integer getIsHavePic() {
        return isHavePic;
    }

    public void setIsHavePic(Integer isHavePic) {
        this.isHavePic = isHavePic;
    }
}
