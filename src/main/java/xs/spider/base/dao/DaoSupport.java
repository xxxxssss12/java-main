package xs.spider.base.dao;

import xs.spider.base.bean.BaseEntity;
import xs.spider.base.bean.PageBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hasee on 2016/8/27.
 */
public interface DaoSupport<T extends BaseEntity, PK extends Serializable> {

    T get(PK id);

    T get(T t) throws Exception;

    List<T> getList(T t) throws  Exception;
    List<T> getList(T t, String orderStr) throws Exception;

    PageBean getPage(T t) throws Exception;

    PageBean getPage(T t, String orderStr) throws Exception;

    Integer getCount(String sql, List<Object> paramList);

    int save(T entity) throws Exception;

    void delete(PK id);

    int update(T entity, boolean isEmptyUp) throws Exception ;
}
