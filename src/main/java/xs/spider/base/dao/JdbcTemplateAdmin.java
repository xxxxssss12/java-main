package xs.spider.base.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import xs.spider.base.bean.BaseEntity;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.base.util.LogUtil;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hasee on 2017/1/25.
 */
public class JdbcTemplateAdmin {
    private JdbcTemplate jdbcTemplate = null;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate = null;
    private DaoSupport<BaseEntity,Integer> sd;

    public JdbcTemplateAdmin() {

    }

    public JdbcTemplateAdmin(DataSource dataSource) {
        if (null == jdbcTemplate || null == jdbcTemplate.getDataSource()) {
            jdbcTemplate = new JdbcTemplate(dataSource);
        }
        if (null == namedParameterJdbcTemplate) {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
                    dataSource);
        }
        if (null == sd) {
            sd = new DaoSupportImpl<BaseEntity,Integer>(jdbcTemplate);
        }
    }

    /**
     * 查询唯一对象
     *
     * @param sql
     * @param args
     * @param requiredType
     * @return
     * @throws DataAccessException
     */
    public <T> T queryForObject(String sql, Object[] args, Class<T> requiredType)
            throws DataAccessException {
        this.log("调用管理员的queryForObject方法", sql);
        return jdbcTemplate.queryForObject(sql, args, requiredType);
    }

    /**
     * 查询列表
     *
     * @param sql
     * @param args
     * @return
     * @throws DataAccessException
     */
    public List<Map<String, Object>> queryForList(String sql, Object... args)
            throws DataAccessException {
        this.log("调用管理员的queryForList方法", sql);
        return jdbcTemplate.queryForList(sql, args);
    }

    /**
     * 查询列表
     *
     * @param sql
     * @param elementType
     * @param args
     * @return
     * @throws DataAccessException
     */
    public <T> List<T> queryForList(String sql, Class<T> elementType,
                                    Object... args) throws DataAccessException {
        this.log("调用管理员的queryForList方法", sql);
        return jdbcTemplate.queryForList(sql, elementType, args);
    }

    /**
     * 查询列表
     *
     * @param sql
     * @param paramMap
     * @return
     * @throws DataAccessException
     */
    public List<Map<String, Object>> queryForList(String sql,
                                                  Map<String, ?> paramMap) throws DataAccessException {
        this.log("调用管理员的queryForList方法", sql);
        return namedParameterJdbcTemplate.queryForList(sql, paramMap);
    }

    /**
     * 分页查询
     *
     * @param clazz
     * @param sql
     * @param paramMap
     * @return
     * @throws Exception
     */
    public List<? extends BaseEntity> findByPageParamMap(Class<? extends BaseEntity> clazz,
                                      String sql, HashMap<String, Object> paramMap) throws Exception {
        this.log("调用管理员的findByPageParamMap方法", sql);
//        if (null != sd) {
//            return this.sd.getPage(clazz, sql, paramMap);
//        } else {
//            return new PagerVO();
//        }
        return null;
    }

    /**
     * 记日志
     *
     * @param log
     */
    private void log(String log, String sql) {
        LogUtil.info(this.getClass(), log + "sql语句：" + sql);
    }

    /**
     * 设置数据源
     *
     * @param dataSource
     */
    public void setDataSource(DataSource dataSource) {
        if (null == jdbcTemplate || null == jdbcTemplate.getDataSource()) {
            jdbcTemplate = new JdbcTemplate(dataSource);
        }
        if (null == namedParameterJdbcTemplate) {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
                    dataSource);
        }
        if (null == sd) {
            sd = new DaoSupportImpl<BaseEntity, Integer>(jdbcTemplate);
        }
    }

    /**
     * 获取数据源用户名
     * @return
     */
//    public String getDataSourceUsername() {
//        if (null != jdbcTemplate) {
//            String user="";
//            try{
//                BasicDataSource ds = (BasicDataSource) jdbcTemplate.getDataSource();
//                user= ds.getUsername();
//            }catch(Exception e){
//                ComboPooledDataSource ds=(ComboPooledDataSource)jdbcTemplate.getDataSource();
//                user=ds.getUser();
//
//            }
//            return user;
//        }
//        return "";
//    }
}
