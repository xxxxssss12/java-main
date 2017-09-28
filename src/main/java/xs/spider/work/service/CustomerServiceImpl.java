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

import java.util.Arrays;
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

    public ResultInfo getInfo(String phone, Integer id) throws Exception {
        if (Util.isBlank(phone) && Util.isBlank(id)) return new ResultInfo(-1, "fail");
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();

        Customer customer = getCustomer(phone, id, sb);
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
        for (String key : obj.keySet()) {
            sb1.append(key + " : " + obj.get(key)).append("<br>\r\n");
        }

        String encodeId = getEncodeId(customer.getId());
        ResultInfo ri = getVipCondition(customer.getId(), encodeId);
        if (ri != null && ri.getCode() == 1) {
            JSONObject vipCondition = ((JSONObject) ri.getData());
            obj.put("creditScore", vipCondition.get("creditScore"));
            obj.put("maxOverdue", vipCondition.get("maxOverdue"));
            obj.put("firstBorrow", vipCondition.get("firstBorrow"));
            obj.put("repay", vipCondition.get("repay"));
            sb1.append(obj.getString("sb"));
        }
        ri = getLevelVipInfo(encodeId);
        if (ri != null && ri.getCode() == 1) {
            JSONObject levelInfo = ((JSONObject) ri.getData());
            obj.put("level", levelInfo.get("levelCode"));
            obj.put("canUpLevel", levelInfo.get("canUpLevel"));
            obj.put("process", levelInfo.get("process"));
            String tempQuotaInfo = getTempquotaInfo(encodeId, ri.getMessage());
            sb1.append(levelInfo.getString("sb")).append(tempQuotaInfo);
        }
        obj.put("sb", sb1
                .append(sb)
                .toString());
        return new ResultInfo(1, "success", obj);
    }
    public String checkLevelVipQuestion(String phone, Integer customerId) throws Exception {
        ResultInfo ri = getInfo(phone, customerId);
        StringBuilder sb = new StringBuilder("");
        if (ri != null && ri.getCode() == 1) {
            JSONObject obj = (JSONObject) ri.getData();
            System.out.println(obj.toJSONString());
            String level = obj.getString("level");
            if (!"bronze".equals(level)) return "没有问题";
            String canUpLevel = obj.getString("canUpLevel");
            Integer process = obj.getInteger("process");

            String creditScore = obj.getString("creditScore");
            String maxOverdue = obj.getString("maxOverdue");
            String firstBorrow = obj.getString("firstBorrow");
            String repay = obj.getString("repay");
            sb.append("当前等级：").append(level)
                    .append(";可升级等级:")
                    .append(canUpLevel).append(";升级进程:").append(process)
                    .append(";四项数据：creditScore=").append(creditScore)
                    .append(";maxOverdue=").append(maxOverdue)
                    .append(";firstBorrow=").append(firstBorrow)
                    .append(";repay=").append(repay);
            return sb.toString();
        }
        return "没找到等级信息";
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
                " where mco.customer_id ='"+id+"' AND should_date < curdate() " +
                " and mco.investor_id not in " +
                " (1,4,5,6,7,9,10,11,13,14,15,16,17,18,25,27,44,50)" +
                " and mcr.repayment_status in (0,1)";
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

    private Customer getCustomer(String phone, Integer id, StringBuilder sb) throws Exception {
        Customer customer = new Customer();
        customer.setId(id);
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
    private static String getEncodeId(Integer customerId) throws Exception {
        String jiamiUrl = "http://as-dev.wecash.net:8989/uncid/q?id=" + customerId + "&type=1";
        ResultInfo ri = HttpClientUtil.doGet(jiamiUrl, null);
        if (ri.getCode() == 1) {
            String resp = (String) ri.getData();
            if (resp.indexOf("customer:") != -1) {
                String encodeId = resp.replace("customer:", "");
                return encodeId.substring(0, encodeId.indexOf("<br>"));
            }
        }
        return null;
    }

    private ResultInfo getVipCondition(Integer customerId, String encodeId) throws Exception {
        try {
            if (Util.isBlank(encodeId)) return null;
            JSONObject res = new JSONObject();
            ResultInfo ri = null;
            String resp = null;
            String url = "http://121.199.39.118:8000/wecash-platform-web/platform/customer/vip?CUSTOMER_ID="
                    + encodeId;
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
                    res.put(_single.getString("code"), _single.get("queryValue"));
                }
                sb.append("<br><br>");
                res.put("sb", sb);
                return new ResultInfo(1, "success", res);
            } else {
                LogUtil.info(getClass(), "vip return:" + JSON.toJSONString(ri));
            }
        } catch (Exception e) {
            LogUtil.error(getClass(), ExceptionWrite.get(e));
        }
        return new ResultInfo(-1, "fail");
    }

    public ResultInfo getLevelVipInfo(String encodeId) {

        String url = "http://121.199.39.118:8000/wecash-platform-web/platform/customer/levelVip/index?CUSTOMER_ID="
                + encodeId;
        JSONObject www = new JSONObject();
        try {
            if (Util.isBlank(encodeId)) return null;
            ResultInfo ri = HttpClientUtil.doGet(url, null);
            if (ri.getCode() == 1) {
                String resp = (String) ri.getData();
                JSONObject obj = JSON.parseObject(resp);
                JSONObject vipInfo = obj.getJSONObject("data").getJSONObject("levelVipInfo");
                JSONObject currentLevelInfo = vipInfo.getJSONObject("levelConfig");
                String canUpLevel = "null";
                if (vipInfo.getJSONObject("canUplevelInfo") != null)
                    canUpLevel = vipInfo.getJSONObject("canUplevelInfo").getString("canUplevel");
                StringBuilder sb = new StringBuilder();
                sb.append("用户当前新vip数据：等级：")
                        .append(currentLevelInfo.getString("levelCode"))
                        .append(";可升级等级：").append(canUpLevel)
                        .append(";升级进程:").append(vipInfo.getString("levelProcess"))
                        .append("<br>");
                sb.append("<br>");
                www.put("levelCode",currentLevelInfo.getString("levelCode") );
                www.put("canUpLevel",canUpLevel);
                www.put("process",vipInfo.getInteger("levelProcess") );
                www.put("sb", sb);
                return new ResultInfo(1, currentLevelInfo.getString("levelCode"), www);
            }
        } catch (Exception e) {
            LogUtil.error(getClass(), ExceptionWrite.get(e));
        }
        return new ResultInfo(-1, "fail");
    }
    private String getTempquotaInfo(String encodeId, String levelCode) {
        if (Util.isBlank(levelCode) || "bronze".equals(levelCode)) {
            return "";
        }
        try {
            String url = "http://121.199.39.118:8000/wecash-platform-web/platform/customer/levelVip/buyTempQuotaInfo?CUSTOMER_ID="
                    + encodeId;
            ResultInfo ri = HttpClientUtil.doGet(url, null);
            StringBuilder sb = new StringBuilder();
            if (ri.getCode()==1) {
                String resp = (String) ri.getData();
                JSONObject obj = JSON.parseObject(resp);
                String errorCode = obj.getString("errorCode");
                if (Util.isBlank(errorCode) || !"already".equals(errorCode)) {
                    JSONObject data = obj.getJSONObject("data");
                    sb = new StringBuilder("可支付:").append(data.getString("canPayTempQuota"));
                } else {
                    sb.append(obj.getString("errorDescription"));
                }
            }
            sb.append("<br>");
            url = "http://121.199.39.118:8000/wecash-platform-web/platform/customer/levelVip/getTempQuotaRecord?CUSTOMER_ID="
                    + encodeId;
            ri = HttpClientUtil.doGet(url, null);
            if (ri.getCode() == 1) {
                JSONObject result = JSON.parseObject(ri.getData().toString());
                Integer isSucc = result.getInteger("successful");
                if (isSucc != null && isSucc == 1) {
                    JSONArray array = result.getJSONObject("data").getJSONArray("list");
                    if (array != null && !array.isEmpty()) {
                        for (int i=0; i<array.size(); i++ ) {
                            sb.append(array.getJSONObject(i).toJSONString()).append("<br>");
                        }
                    }
                }
            }
            sb.append("<br>");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "<br>";
    }

    public static void main(String[] args) throws Exception {
        List<Integer> custIds = Arrays.asList(10737159,
                13878771,
                9888781,
                18701495,
                9793631,
                16347553,
                16511748,
                15589393,
                646948,
                15835750,
                13559821,
                11725448,
                22588197,
                22806147);
        for (Integer custId : custIds) {
            String encodeId = getEncodeId(custId);
            String url = "http://121.199.39.118:8000/wecash-platform-web/platform/customer/levelVip/cleanCache?CUSTOMER_ID="
                    + encodeId;
            System.out.println(JSON.toJSONString(HttpClientUtil.doGet(url, null)));
        }
    }
}
