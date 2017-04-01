package xs.spider.base.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import xs.account.common.bean.ResultInfo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * http连接调用接口实体类
 * @author hasee
 *
 */
public class HttpClientUtil {
	/**
	 * get方式调用http地址
	 * @param url
	 * @param parammap
	 * @return
	 * @throws Exception
	 */
	public static ResultInfo doGet(String url, Map<String, Object> parammap) throws Exception {
		if (Util.isBlank(url)) return null;
		ResultInfo ri = new ResultInfo(1, "success");
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	StringBuffer url_final = new StringBuffer(url);
        	if (url.indexOf("?") == -1) {
        		url_final.append("?");
        	}
            StringBuffer params = new StringBuffer();
            if (!Util.isBlank(parammap)) {
	            for (Entry<String, Object> entry : parammap.entrySet()) {
	            	String key = entry.getKey();
	            	Object value = entry.getValue();
	            	params.append("&" + key + "=" + value);
	            }
            }
            if (params.length()>0) {
            	url_final.append(params.substring(1, params.length()));
            }
            LogUtil.info(HttpClientUtil.class, "doget url:" + url_final.toString());
            HttpGet httpGet = new HttpGet(url_final.toString());
            System.out.println(url_final.toString());
            InputStreamReader isr = null;
            InputStream is = null;
            CloseableHttpResponse response = httpclient.execute(httpGet);
//            response1.get
            // The underlying HTTP connection is still held by the response object
            // to allow the response content to be streamed directly from the network socket.
            // In order to ensure correct deallocation of system resources
            // the user MUST call CloseableHttpResponse#close() from a finally clause.
            // Please note that if response content is not fully consumed the underlying
            // connection cannot be safely re-used and will be shut down and discarded
            // by the connection manager.
            try {
                System.out.println(response.getStatusLine());
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                StringBuffer sb = new StringBuffer();
                isr = new InputStreamReader(is);
                char[] buffer = new char[1024];
                int len = -1;
                while ((len = isr.read(buffer)) != -1) {
                	sb.append(buffer, 0, len);
                }
                EntityUtils.consume(entity);
                System.out.println(sb.toString());
            } catch(Exception e) {
            	ri.setCode(-2);
            	ri.setMessage(e.getMessage());
            	ri.setData(ExceptionWrite.get(e));
            	LogUtil.error(HttpClientUtil.class, ExceptionWrite.get(e));
            } finally {
                response.close();
            }
        } catch(Exception e) {
        	ri.setCode(-1);
        	ri.setMessage(e.getMessage());
        	ri.setData(ExceptionWrite.get(e));
        	LogUtil.error(HttpClientUtil.class, ExceptionWrite.get(e));
        } finally {
            httpclient.close();
        }
		return null;
		
	}
	/**
	 * post方式请求http地址
	 * @param url
	 * @param parammap
	 * @return
	 * @throws Exception
	 */
	public static ResultInfo doPost(String url, Map<String, Object> parammap) throws Exception {
		if (Util.isBlank(url)) return null;
		LogUtil.info(HttpClientUtil.class, "dopost url:" + url + ";param:" + JsonUtil.beanToJson(parammap));
		ResultInfo ri = new ResultInfo(1, "success");
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	HttpPost httpPost = new HttpPost(url);
            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            if (!Util.isBlank(parammap) && parammap.size() > 0) {
	            for (String key : parammap.keySet()) {
	            	nvps.add(new BasicNameValuePair(key, Util.null2string(parammap.get(key))));
	            }
	        }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            CloseableHttpResponse response = httpclient.execute(httpPost);
            InputStreamReader isr = null;
            InputStream is = null;
            try {
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                StringBuffer sb = new StringBuffer();
                isr = new InputStreamReader(is);
                char[] buffer = new char[1024];
                int len = -1;
                while ((len = isr.read(buffer)) != -1) {
                	sb.append(buffer, 0, len);
                }
                EntityUtils.consume(entity);
                ri.setData(sb.toString());
            } catch(Exception e) {
            	ri.setCode(-2);
            	ri.setMessage(e.getMessage());
            	ri.setData(ExceptionWrite.get(e));
            	LogUtil.error(HttpClientUtil.class, ExceptionWrite.get(e));
            } finally {
                response.close();
            }
        } catch(Exception e) {
        	ri.setCode(-1);
        	ri.setMessage(e.getMessage());
        	ri.setData(ExceptionWrite.get(e));
        	LogUtil.error(HttpClientUtil.class, ExceptionWrite.get(e));
        } finally {
        	httpclient.close();
		}
		return ri;
	}
	public static void main(String[] args) throws Exception {
	}
}
