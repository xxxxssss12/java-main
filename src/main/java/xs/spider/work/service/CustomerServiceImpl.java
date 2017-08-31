package xs.spider.work.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.dao.DaoSupport;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.base.util.ExceptionWrite;
import xs.spider.base.util.HttpClientUtil;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.Util;
import xs.spider.work.bean.AmountChangeRecord;
import xs.spider.work.bean.CreditCardAttr;
import xs.spider.work.bean.Customer;
import xs.spider.work.bean.RepaymentInfo;
import xs.spider.work.dao.AmountChangeRecordDao;
import xs.spider.work.dao.CreditCardAttrDao;

import java.util.Collections;
import java.util.List;

/**
 * Created by xs on 2017/8/30
 */
@Service
public class CustomerServiceImpl extends DaoSupportImpl<Customer, Integer> implements DaoSupport<Customer, Integer> {
    @Autowired
    private AmountChangeRecordDao amountChangeRecordDao;
    @Autowired
    private CreditCardAttrDao creditCardAttrDao;

    public ResultInfo getInfo(String phone) throws Exception {
        if (Util.isBlank(phone)) return new ResultInfo(-1, "fail");
        StringBuilder sb = new StringBuilder();
        Customer customer = getCustomer(phone, sb);
        if (customer == null) return new ResultInfo(-1, "没有找到customer");
        LogUtil.info(getClass(), "customer=" + JSON.toJSONString(customer));
        // 查询vip标志
        String vipFlag = getVipFlag(customer);
        // 降额标志
        boolean isDecrUserAmount = getIsDecrUserAmount(customer.getId(), sb);
        // 还款逾期
        boolean isOverdue = getIsOverdue(customer.getId(), sb);
        boolean isFreeze = getIsFreeze(customer.getId());
        JSONObject obj = new JSONObject();
        obj.put("是否成为vip" , vipFlag);
        obj.put("是否降额" ,  isDecrUserAmount);
        obj.put("是否逾期" , isOverdue);
        obj.put("是否冻结" , isFreeze);
        StringBuilder sb1 = new StringBuilder();
        for (String key : obj.keySet()) {
            sb1.append(key + " : " + obj.get(key)).append("<br>\r\n");
        }
        obj.put("sb", sb1.append(getVipCondition(customer.getId())).append(sb).toString());
        return new ResultInfo(1, "success", obj);
    }

    private boolean getIsFreeze(Integer id) {
        String sql = "select is_freeze from mcc_credit_card where customer_id=" +id;
        Integer isFreeze = getJdbcTemplate().queryForObject(sql, Integer.class);
        if (isFreeze == null || isFreeze == 0) return false;
        return true;
    }

    private boolean getIsOverdue(Integer id, StringBuilder sb) {
        boolean flag = false;
        RepaymentInfo info = new RepaymentInfo();
        String sql = "select customer_id, investor_id, should_date, mcr.overdue_day,end_date, mcr.repayment_status,mcr.overdue_amount,mcr.should_amount " +
                " from mcc_consume_repayment  mcr " +
                " LEFT JOIN mcc_customer_consume  mco on mcr.consume_id =  mco.id " +
                " where mco.customer_id ='"+id+"' AND should_date < curdate();";
        List<RepaymentInfo> list = getJdbcTemplate().query(sql, new Object[]{}, new BeanPropertyRowMapper<RepaymentInfo>(RepaymentInfo.class));
        if (list == null || list.isEmpty()) flag = false;
        else {
            for (RepaymentInfo bean : list) {
                if (bean.getOverdue_day() != 0)
                    sb.append("repayment: " + JSON.toJSONString(bean, SerializerFeature.WriteDateUseDateFormat)).append("<br>\r\n");
                LogUtil.info(getClass(), "repayment: " + JSON.toJSONString(bean, SerializerFeature.WriteDateUseDateFormat));
                if (bean.getRepayment_status() != 2 || Util.isBlank(bean.getEnd_date())) flag = true;
            }
            sb.append("<br>");
        }
        return flag;
    }

    private boolean getIsDecrUserAmount(Integer customerId, StringBuilder sb) throws Exception {
        boolean flag = false;
        AmountChangeRecord record = new AmountChangeRecord();
        record.setCustomer_id(customerId);
        List<AmountChangeRecord> records = amountChangeRecordDao.getList(record);
        if (records == null || records.isEmpty()) flag = false;
        else {
            for (AmountChangeRecord bean : records) {
                if (bean.getAmount_change() != null && bean.getAmount_change() != 0)
                    sb.append("amountChange: " + JSON.toJSONString(bean, SerializerFeature.WriteDateUseDateFormat)).append("<br>\r\n");
                LogUtil.info(getClass(), "amountChange: " + JSON.toJSONString(bean, SerializerFeature.WriteDateUseDateFormat));
                if (bean.getAmount_change() != null && bean.getAmount_change() < 0)
                    flag = true;
            }
            sb.append("<br>");
        }
        return flag;
    }

    private String getVipFlag(Customer customer) throws Exception {
        Integer customerId = customer.getId();
        LogUtil.info(getClass(), "getVipFlag:customerId=" + customerId);
        CreditCardAttr attr = new CreditCardAttr();
        attr.setCustomer_id(customerId);
        attr.setStatus(1);
        attr = creditCardAttrDao.get(attr);
        if (attr == null) {
            LogUtil.info(getClass(), "error:attr=null");
            return "0";
        }
        return attr.getAttr_value();
    }

    private Customer getCustomer(String phone, StringBuilder sb) throws Exception {
        Customer customer = new Customer();
        customer.setPhone(phone);
        customer.setLogout("0");
        List<Customer> customerList = this.getList(customer);
        if (customerList == null || customerList.isEmpty()) return null;
        for (Customer bean : customerList) {
            sb.append("customer: " + JSON.toJSONString(bean, SerializerFeature.WriteDateUseDateFormat)).append("<br>\r\n");
            LogUtil.info(getClass(), "customer: " + JSON.toJSONString(bean, SerializerFeature.WriteDateUseDateFormat));
            if (bean != null && !Util.isBlank(bean.getShare_code())
                    && Util.isBlank(bean.getChannel_cust_id())
                    && Util.isBlank(bean.getChannel_id())) {
                sb.append("<br>");
                return bean;
            }
        }
        sb.append("<br>");
        return null;
    }

    private String getVipCondition(Integer customerId) throws Exception {
        try {
            String jiamiUrl = "http://as-dev.wecash.net:8989/uncid/q?id=" + customerId + "&type=1";
            ResultInfo ri = HttpClientUtil.doGet(jiamiUrl, null);
            if (ri.getCode() == 1) {
                String resp = (String) ri.getData();
                if (resp.indexOf("customer:") != -1) {
                    String encodeId = resp.replace("customer:", "");
                    encodeId = encodeId.substring(0, encodeId.indexOf("<br>"));
                    String url = "http://m.wecash.net/platform/customer/vip?CUSTOMER_ID=" + encodeId;
                    ri = HttpClientUtil.doGet(url, null);
                    if (ri.getCode() == 1) {
                        resp = (String) ri.getData();
                        JSONObject obj = JSON.parseObject(resp);
                        JSONObject vipInfo = obj.getJSONObject("data").getJSONObject("vipInfo");
                        StringBuilder sb = new StringBuilder();
                        sb.append("encodeId=" + encodeId).append("...满足").append(vipInfo.getString("vipStatus")).append("项<br>");
                        JSONArray condition = vipInfo.getJSONArray("vipConditionList");
                        for (Object single : condition) {
                            JSONObject _single = JSON.parseObject(JSON.toJSONString(single));
                            sb.append(_single.getString("name")).append(":").append(_single.getString("queryValue")).append(", ");
                        }
                        sb.append("<br><br>");
                        return sb.toString();
                    } else {
                        LogUtil.info(getClass(), "vip return:" + JSON.toJSONString(ri));
                    }
                }
            } else {
                LogUtil.info(getClass(), "jiamiUrl return:" + JSON.toJSONString(ri));
            }
        } catch (Exception e) {
            LogUtil.error(getClass(), ExceptionWrite.get(e));
        }
        return "";
    }
}
