package xs.spider.base.util.http;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;

import java.util.List;

/**
 * Created by xs on 2017/4/4.
 */
public class HttpRespBean {
    private Integer code;
    private String message;
    private String responseBody;
    private List<Header> headers;
    private List<Cookie> cookies;
    public HttpRespBean() {
    }

    public HttpRespBean(Integer code, String message, String responseBody, List<Header> headers, List<Cookie> cookies) {
        this.code = code;
        this.message = message;
        this.responseBody = responseBody;
        this.headers = headers;
        this.cookies = cookies;
    }
    public HttpRespBean(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public void setCodeAndMsg(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
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

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
