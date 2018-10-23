package xs.spider.main;

import com.alibaba.fastjson.JSON;
import org.apache.http.Header;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.http.HttpClientUtil;
import xs.spider.base.util.http.HttpReqBean;
import xs.spider.base.util.http.HttpRespBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoTest {
    private static Logger log = LogUtil.getLogger(AutoTest.class);
    private static RequestBean loginRequest;
    private static BasicCookieStore cookieStore = new BasicCookieStore();
    private static CloseableHttpClient httpclient;
    static {
        loginRequest = new RequestBean();
        Map<String, String> params = new HashMap<>();
        params.put("username", "xiongshun");
        params.put("password", "hKE5*WQta4^KS41!");
        loginRequest.setUrl("http://118.31.75.61/cbWeb/auth/doLogin");
        loginRequest.setParams(params);
    }

    public static void doAutoTest() {
        httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();

        try {
            // 1. 登陆
            ResultInfo result = login();
            // 3. 获取接口信息
            List<RequestBean> beanList = getRequestList();
            // 4. 批量请求
            ResultInfo ri = batchRequest(beanList);
            // 5. 解析返回结果
//            analysResult(ri);
        } catch(Exception e) {
            log.error("error!", e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static ResultInfo batchRequest(List<RequestBean> beanList) {
        for (RequestBean bean : beanList) {
            ResultInfo ri = singleRequest(bean);
        }
        return ResultInfo.build();
    }

    private static ResultInfo singleRequest(RequestBean bean) {
        long startTime = System.currentTimeMillis();
        HttpReqBean reqBean = new HttpReqBean();
        reqBean.setUrl(bean.getUrl());
        if (bean.getParams() != null && !bean.getParams().isEmpty()) {
            reqBean.setParams(bean.getParams());
        }
        if (bean.getHeaders() != null && !bean.getHeaders().isEmpty()) {
            List<Header> headerList = new ArrayList<>();
            for (Map.Entry<String, String> entry : bean.getHeaders().entrySet()) {
                Header header = new BasicHeader(entry.getKey(), entry.getValue());
                headerList.add(header);
            }
            reqBean.setHeaders(headerList);
        }
        if ("POST".equalsIgnoreCase(bean.getMethod())) {
            reqBean.setType(1);
        } else {
            reqBean.setType(0);
        }
        HttpRespBean respBean = HttpClientUtil.httpRequest(reqBean, httpclient, cookieStore);
        long endTime = System.currentTimeMillis();
        log.info("singleRequestEnd!!!url={},in={},resultcode={}out={},hast={}ms",
                bean.getUrl(), JSON.toJSONString(bean.getParams()), respBean.getCode(), respBean.getMessage(), endTime - startTime);
        return ResultInfo.build(respBean);
    }

    private static List<RequestBean> getRequestList() {
        List<RequestBean> list = new ArrayList<>();
        RequestBean bean = new RequestBean();
        bean.setUrl("http://118.31.75.61/cbWeb/modelAll/add");
        Map<String, String> params = new HashMap<>();
        params.put("modelNo", "ttt111222");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
        bean.setHeaders(headers);
        bean.setParams(params);
        bean.setMethod("POST");
        list.add(bean);
        return list;
    }

    private static ResultInfo login() {
        HttpReqBean reqBean = new HttpReqBean();
        reqBean.setUrl(loginRequest.getUrl());
        reqBean.setParams(loginRequest.getParams());
        HttpRespBean respBean = HttpClientUtil.doPost(reqBean, httpclient, cookieStore);
        log.info("登陆:请求url={},返回值={}", reqBean.getUrl(), JSON.toJSONString(respBean));
        return ResultInfo.build(respBean);
    }
}


class RequestBean {
    private String method;
    private String url;
    private Map<String, String> params;
    private Map<String, String> headers;
    private List<Cookie> cookies;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}

class Cookie {
    private String name;
    private String value;
    private String domain;
    private String path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}