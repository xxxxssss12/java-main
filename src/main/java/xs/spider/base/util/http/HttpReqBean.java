package xs.spider.base.util.http;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import xs.spider.base.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xs on 2017/4/4.
 */
public class HttpReqBean {
    private String url;
    private Map<String, Object> params;
    private Integer type; //0=get; 1=post;
    private List<Header> headers;
    private List<Cookie> cookies;
    private List<NameValuePair> paramList;
    private String paramStr;

    public HttpReqBean() {
        this.paramStr = "";
    }

    public HttpReqBean(String url, Map<String, Object> params, Integer type, List<Header> headers, List<Cookie> cookies) {
        this.url = url;
        this.params = params;
        this.type = type;
        this.headers = headers;
        this.cookies = cookies;
        if (params != null && !params.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            List<NameValuePair> list = new ArrayList<>();
            for (String key : params.keySet()) {
                String value = Util.null2string(params.get(key));
                sb.append("&").append(key).append("=").append(value);
                list.add(new BasicNameValuePair(key,value));
            }
            this.paramList = list;
            this.paramStr=sb.substring(1);
        } else {
            this.paramStr = "";
        }
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    public Map<String, Object> getParams() {
        return params;
    }
    public Object transformParams() {
        if (type == null || type != 1) {
            return paramStr;
        } else {
            if (paramList == null) paramList = new ArrayList<>();
            return paramList;
        }
    }
    public void setParams(Map<String, Object> params) {
        this.params = params;
        if (null != params && !params.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            List<NameValuePair> list = new ArrayList<>();
            for (String key : params.keySet()) {
                String value = Util.null2string(params.get(key));
                sb.append("&").append(key).append("=").append(value);
                list.add(new BasicNameValuePair(key,value));
            }
            this.paramList = list;
            this.paramStr=sb.substring(1);
        } else {
            this.paramStr = "";
        }
    }

    public List<NameValuePair> getParamList() {
        return paramList;
    }

    public String getParamStr() {
        return paramStr;
    }
}
