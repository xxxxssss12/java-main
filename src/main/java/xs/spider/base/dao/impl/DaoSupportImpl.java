package xs.spider.base.dao.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import xs.spider.base.bean.BaseEntity;
import xs.spider.base.bean.PageBean;
import xs.spider.base.dao.DaoSupport;
import xs.spider.base.pager.PageContext;
import xs.spider.base.util.BeanUtil;
import xs.spider.base.util.ExceptionWrite;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.Util;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * Created by hasee on 2016/8/28.
 */
public class DaoSupportImpl<T extends BaseEntity, PK extends Serializable> implements DaoSupport<T,PK> {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    private Class<T> clazz;
    protected JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    protected NamedParameterJdbcTemplate getNamedJdbcTemplate() {
        if (namedJdbcTemplate == null) namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
        return namedJdbcTemplate;
    }
    public DaoSupportImpl() {
        // 使用反射技术得到T的真实类型
        try {
            ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass(); // 获取当前new的对象的 泛型的父类 类型
            this.clazz = (Class<T>) pt.getActualTypeArguments()[0]; // 获取第一个类型参数的真实类型
        } catch (Exception e) {
            LogUtil.info(getClass(), "something wrong:" + ExceptionWrite.get(e));
        }
    }
    public DaoSupportImpl(JdbcTemplate jdbcTemplate) {
        // 使用反射技术得到T的真实类型
        try {
            ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass(); // 获取当前new的对象的 泛型的父类 类型
            this.clazz = (Class<T>) pt.getActualTypeArguments()[0]; // 获取第一个类型参数的真实类型
            this.jdbcTemplate = jdbcTemplate;
        } catch (Exception e) {
            LogUtil.info(getClass(), "something wrong:" + ExceptionWrite.get(e));
        }
    }
    @Override
    public T get(PK id) {
        try {
            BaseEntity baseEntity = clazz.newInstance();
            StringBuffer sql = new StringBuffer(" SELECT * FROM ")
                    .append(baseEntity.getTableName())
                    .append(" dtb WHERE dtb.")
                    .append(baseEntity.getPkName()).append(" =? ");
            List<T> list = jdbcTemplate.query(sql.toString(), new Object[]{id}, new BeanPropertyRowMapper<T>(clazz));
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            LogUtil.error(getClass(), "something wrong:" + ExceptionWrite.get(e));
            return null;
        }
    }

    @Override
    public T get(T t)  {
        List<T> result = this.getList(t);
        if (result == null || result.isEmpty())
            return null;
        else
            return result.get(0);
    }

    @Override
    public List<T> getList(T t) {
        return getList(t, null);

    }

    @Override
    public List<T> getList(T t, String orderStr) {
        if (t==null) return null;
        List<Object> paramlist = new ArrayList<>();
        StringBuffer sql = getListBaseSql(t, paramlist);
        if (Util.isBlank(sql)) return null;
        if (!Util.isBlank(orderStr)) {
            sql.append(" order by ").append(orderStr);
        }
        List<T> result = jdbcTemplate.query(sql.toString(), paramlist.toArray(), new BeanPropertyRowMapper<T>(clazz));
        if (Util.isBlank(result) || result.isEmpty()) {
            return null;
        } else {
            return result;
        }
    }

    private StringBuffer getListBaseSql(T t, List<Object> paramlist) {
        if (null == paramlist) return null;
        StringBuffer sql = new StringBuffer(" SELECT * from " + t.getTableName() + " dtb  where 1=1 ");
        StringBuffer where = null;
        List<String> attrNames = t.gotAttrNames();
        where = getWhereCondition(t, attrNames, paramlist);
        if (Util.isBlank(where)) {
//            return null;
        } else {
            sql.append(where);
        }
        return sql;
    }

    private StringBuffer getWhereCondition(T t, List<String> attrNames, List<Object> paramlist) {
        StringBuffer where = new StringBuffer();
        for (String name : attrNames) {
            Object value = t.getAttributeValue(name);
            if (!Util.isBlank(value)) {
                if ((!(value instanceof Collection))&& (!(value instanceof Map))) {
                    where.append(" and dtb." + name + "=?");
                    paramlist.add(value);
                }
            }
        }
        return where;
    }
    @Override
    public PageBean getPage(T t) throws Exception {
        return getPage(t, null);
    }
    @Override
    public PageBean<T> getPage(String sql, List<Object> paramlist) {
        Integer pageNum = PageContext.getPageNum();
        Integer pageSize = PageContext.getPageSize();
        if (Util.isBlank(pageNum) || Util.isBlank(pageSize)) {
            return null;
        }
        Integer startLine = (pageNum-1) * pageSize;
        PageBean page = new PageBean();
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        page.setTotal(getCount(sql.toString(), paramlist));
        if (page.getTotal() == null) {
            page.setCode(-1);
            return page;
        }
        sql += " LIMIT " + startLine + "," + pageSize;
        try {
            List<T> result = jdbcTemplate.query(sql.toString(), paramlist==null?null:paramlist.toArray(), new BeanPropertyRowMapper<T>(clazz));
            page.setCode(1);
            if (Util.isBlank(result) || result.isEmpty()) {
                return page;
            } else {
                page.setData(result);
                return page;
            }
        } catch (Exception e) {
            LogUtil.error(getClass(), ExceptionWrite.get(e));
            page.setCode(-1);
            return page;
        }
    }
    @Override
    public PageBean<T> getPage(T t, String orderStr) throws Exception {
        Integer pageNum = PageContext.getPageNum();
        Integer pageSize = PageContext.getPageSize();
        if (Util.isBlank(pageNum) || Util.isBlank(pageSize)) {
            return null;
        }
        Integer startLine = (pageNum-1) * pageSize;
        List<Object> paramlist = new ArrayList<>();
        StringBuffer sql = getListBaseSql(t, paramlist);
        if (sql == null) {
            sql = new StringBuffer( "SELECT * from " + BeanUtil.getTableName(clazz) + " dtb ");
        }
        PageBean page = new PageBean();
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        page.setTotal(getCount(sql.toString(), paramlist));
        if (page.getTotal() == null) {
            page.setCode(-1);
            return page;
        }
        if (!Util.isBlank(orderStr)) sql.append(" ORDER BY " + orderStr + " ");
        sql.append(" LIMIT " + startLine + "," + pageSize);
        try {
            List<T> result = jdbcTemplate.query(sql.toString(), paramlist.toArray(), new BeanPropertyRowMapper<T>(clazz));
            page.setCode(1);
            if (Util.isBlank(result) || result.isEmpty()) {
                return page;
            } else {
                page.setData(result);
                return page;
            }
        } catch (Exception e) {
            LogUtil.error(getClass(), ExceptionWrite.get(e));
            page.setCode(-1);
            return page;
        }
    }
    @Override
    public PageBean<T> getPage(T t, Integer num, Integer size) throws Exception {
        return getPage(t, null, num, size);
    }

    @Override
    public PageBean<T> getPage(T t, String orderStr, Integer num, Integer size) throws Exception {
//        Integer pageNum = PageContext.getPageNum();
//        Integer pageSize = PageContext.getPageSize();
        Integer pageNum = num;
        Integer pageSize = size;
        if (Util.isBlank(pageNum) || Util.isBlank(pageSize)) {
            return null;
        }
        Integer startLine = (pageNum-1) * pageSize;
        List<Object> paramlist = new ArrayList<>();
        StringBuffer sql = getListBaseSql(t, paramlist);
        if (sql == null) {
            sql = new StringBuffer( "SELECT * from " + t.getTableName() + " dtb ");
        }
        PageBean page = new PageBean();
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        page.setTotal(getCount(sql.toString(), paramlist));
        if (page.getTotal() == null) {
            page.setCode(-1);
            return page;
        }
        if (!Util.isBlank(orderStr)) sql.append(" ORDER BY " + orderStr + " ");
        sql.append(" LIMIT " + startLine + "," + pageSize);
        try {
            List<T> result = jdbcTemplate.query(sql.toString(), paramlist.toArray(), new BeanPropertyRowMapper<T>(clazz));
            page.setCode(1);
            if (Util.isBlank(result) || result.isEmpty()) {
                return page;
            } else {
                page.setData(result);
                return page;
            }
        } catch (Exception e) {
            LogUtil.error(getClass(), ExceptionWrite.get(e));
            page.setCode(-1);
            return page;
        }
    }
    @Override
    public Integer getCount(String sql, List<Object> paramList) {
        try {
            StringBuffer finalSql = new StringBuffer();
            finalSql.append(" SELECT COUNT(*) FROM (").append(sql).append(") cnt");
            return jdbcTemplate.queryForObject(finalSql.toString(), paramList==null?null:paramList.toArray(), Integer.class);
        } catch (Exception e) {
            LogUtil.error(getClass(), "get Count error!" + ExceptionWrite.get(e));
            return null;
        }
    }
    @Override
    public int save(T entity) {
        StringBuffer sql = new StringBuffer(" INSERT INTO ")
                .append(entity.getTableName());
        StringBuffer finalsb = new StringBuffer("INSERT INTO " + entity.getTableName() + " (");
        StringBuffer paramsb = new StringBuffer();
        StringBuffer valuesb = new StringBuffer("(");
        List<String> attrNames = entity.gotAttrNames();
        List<Object> setValueList = new ArrayList<>();
        Map<String, Object> parammap = new HashMap<>();
        for (String name : attrNames) {
            Object value = entity.getAttributeValue(name);
            if (!Util.isBlank(value) && !BeanUtil.isUserDefine(entity.getClass(), name)) {
                paramsb.append(" " + name + ",");
                valuesb.append(":"+name+",");
                parammap.put(name, value);
            }
        }
        if ( parammap.isEmpty()) {
            return -1;
        } else {
            finalsb.append(paramsb.substring(0, paramsb.length()-1))
                    .append(") values ")
                    .append(valuesb.substring(0, valuesb.length()-1))
                    .append(")");
            KeyHolder holder = new GeneratedKeyHolder();
            int index = getNamedJdbcTemplate().update(finalsb.toString()
                    , new BeanPropertySqlParameterSource(entity)
                    ,holder
                    ,new String[]{entity.getPkName()});
            Number num = holder.getKey();
            if (null != num) {
                entity.setAttributeValue(entity.getPkName(), num.intValue());
                return num.intValue();
            } else {
                return -1;
            }
        }
    }

    @Override
    public void delete(PK id) {
        try {
            BaseEntity baseEntity = clazz.newInstance();
            String sql = "DELETE FROM " + baseEntity.getTableName() + " where " + baseEntity.getPkName() + "=?";
            jdbcTemplate.update(sql, id);
        }catch (Exception e) {
            LogUtil.error(getClass(), "delete wrong:" + ExceptionWrite.get(e));
        }
    }

    @Override
    public int update(T entity, boolean isEmptyUp) {
        try {
            StringBuffer sb = new StringBuffer(" update ");
            sb.append(entity.getTableName()).append(" dtb set ");
            String where = null;
            List<String> attrNames = entity.gotAttrNames();
            List<Object> setValueList = new ArrayList<>();
            for (String name : attrNames) {
                Object value = entity.getAttributeValue(name);
                if (entity.getPkName().equals(name)) {
                    if (Util.isBlank(entity.getAttributeValue(name))) {
                        return -1;
                    }
                    where = " where dtb." + name + " = " + value + " ";
                } else {
                    if (isEmptyUp) {
                        sb.append(" dtb.").append(name).append("=");
                        if (Util.isBlank(value)) {
                            sb.append("null ");
                        } else {
                            sb.append("?,");
                            setValueList.add(value);
                        }
                    } else if (!Util.isBlank(value)) {
                        sb.append(" dtb." + name + " = " + "?,");
                        setValueList.add(value);
                    }
                }
            }
            if (Util.isBlank(setValueList)) return -1;
            String sql = sb.substring(0, sb.length()-1) + where;
            return jdbcTemplate.update(sql, setValueList.toArray());
        } catch (Exception e) {
            LogUtil.error(getClass(), "update wrong:" + ExceptionWrite.get(e));
            return -1;
        }
    }

    public PageBean findByMapPage(String sql, List paramList, Integer num, Integer size) {
        PageBean page = new PageBean();
        page.setPageNum(num);
        page.setPageSize(size);
        try {
            page.setTotal(getCount(sql, paramList));
            if (sql.toLowerCase().indexOf("limit") == -1) {
                int startline = (page.getPageNum()-1) * page.getPageSize();
                sql += " limit " + startline + "," + page.getPageSize();
            }
            List<Map<String, Object>> result = getJdbcTemplate().queryForList(sql, paramList.toArray());
            page.setData(result);
            page.setCode(1);
            return page;
        } catch (Exception e) {
            LogUtil.error(getClass(), "findByMapPage error!" + ExceptionWrite.get(e));
            page.setCode(-1);
            return page;
        }
    }
}
