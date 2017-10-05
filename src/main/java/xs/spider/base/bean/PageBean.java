package xs.spider.base.bean;

import java.util.List;

/**
 * Created by hasee on 2017/1/25.
 */
public class PageBean<T> {
    private Integer pageNum;
    private Integer pageSize;
    private List<T> data;
    private Integer code;
    private Integer total;
    public PageBean() {

    }

    public PageBean(Integer code) {
        this.code = code;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
