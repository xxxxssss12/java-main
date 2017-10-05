package xs.spider.base.util.esutils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.util.StringUtils;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.Util;
import xs.spider.base.util.http.HttpClientUtil;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by xs on 2017/9/24
 */
public class ElasticSearchUtil {
    private static String host;
    private static Integer port;
    private static Integer timeOutMs;

    public void setHost(String host) {
        ElasticSearchUtil.host = host;
    }

    public void setPort(Integer port) {
        ElasticSearchUtil.port = port;
    }

    public void setTimeOutMs(Integer timeOutMs) {
        ElasticSearchUtil.timeOutMs = timeOutMs;
    }

    @PostConstruct
    public void init() {
        if (port == null) port=9200;
        if (timeOutMs == null) timeOutMs=-1;
    }
    public static ResultInfo insert(String index, String type, String id, JSONObject requestBody) throws ConfigLostException {
        String url = getUrl(index, type, id);
        return EsConnectUtil.put(url, requestBody.toJSONString(), timeOutMs);
    }
    public static ResultInfo search(String index, String type, String id, JSONObject requestBody) throws ConfigLostException {
        String url = getUrl(index, type, id) + "_search";
        return EsConnectUtil.post(url, requestBody.toJSONString(), timeOutMs);
    }
    private static String getUrl(String index, String type, String id) throws ConfigLostException {
        if (StringUtils.isEmpty(host)) throw new ConfigLostException("host is Empty");
        StringBuilder sb = new StringBuilder();
        sb.append("http://").append(host)
                .append(":").append(port).append("/")
                .append(index).append("/");
        if (!Util.isBlank(type)) sb.append(type).append("/");
        if (!Util.isBlank(id)) sb.append(id);
        return sb.toString();
    }
}

class EsConnectUtil {

    public static ResultInfo put(String url, String jsonBody, Integer timeOutMs) {
        if (timeOutMs == null) timeOutMs = -1;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(timeOutMs).setConnectionRequestTimeout(timeOutMs)
                    .setSocketTimeout(timeOutMs).build();
            HttpPut put = new HttpPut(url);
            put.setConfig(requestConfig);
            put.setHeader("Accept", "application/json");
            put.addHeader("Content-type","application/json; charset=utf-8");
            put.setEntity(new StringEntity(jsonBody, Charset.forName("UTF-8")));
            try (CloseableHttpResponse response = client.execute(put)) {
                return respToBean(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResultInfo(-999, "异常");
    }
    private static ResultInfo respToBean(HttpResponse httpResponse) {
        if (httpResponse == null) return new ResultInfo(-1, "httpResponse 为空");
        Integer statusLine = httpResponse.getStatusLine().getStatusCode();
        if (statusLine>=400) return new ResultInfo(-2, "reqErr:"+statusLine);
        ResultInfo ri = new ResultInfo();
        ri.setCodeAndMsg(1, "success:" + statusLine);
        HttpEntity entity = httpResponse.getEntity();
        ri.setData(entityToString(entity));
        return ri;
    }
    private static String entityToString(HttpEntity entity) {
        if (entity == null) return null;
        try {
            InputStream is = entity.getContent();
            return Util.streamToStr(is, "UTF-8");
        } catch (IOException e) {
            LogUtil.error(HttpClientUtil.class, e, "entityToString Error");
        }
        return null;
    }

    public static ResultInfo post(String url, String jsonBody, Integer timeOutMs) {
        if (timeOutMs == null) timeOutMs = -1;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(timeOutMs).setConnectionRequestTimeout(timeOutMs)
                    .setSocketTimeout(timeOutMs).build();
            HttpPost get = new HttpPost(url);
            get.setConfig(requestConfig);
            get.setHeader("Accept", "application/json");
            get.addHeader("Content-type","application/json; charset=utf-8");
            get.setEntity(new StringEntity(jsonBody, Charset.forName("UTF-8")));
            try (CloseableHttpResponse response = client.execute(get)) {
                return respToBean(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResultInfo(-999, "异常");
    }
}
