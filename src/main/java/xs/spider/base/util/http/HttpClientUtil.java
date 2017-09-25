package xs.spider.base.util.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.init.Log4jInit;
import xs.spider.base.util.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
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
	public static ResultInfo doGet(String url, Map<String, Object> parammap) throws IOException {
		if (Util.isBlank(url)) return null;
		CookieStore cookieStore = new BasicCookieStore();
		ResultInfo ri = new ResultInfo(1, "success");
		CloseableHttpClient httpclient = HttpClients.custom()
				.setDefaultCookieStore(cookieStore)
				.build();
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
	/**
	 * post方式请求http地址
	 * @param url
	 * @param parammap
	 * @return
	 * @throws Exception
	 */
	public static ResultInfo doPost(String url, Map<String, Object> parammap) throws Exception {
		if (Util.isBlank(url)) return null;
		CookieStore cookieStore = new BasicCookieStore();
		LogUtil.info(HttpClientUtil.class, "dopost url:" + url + ";param:" + JsonUtil.beanToJson(parammap));
		ResultInfo ri = new ResultInfo(1, "success");
		CloseableHttpClient httpclient = HttpClients.custom()
				.setDefaultCookieStore(cookieStore)
				.build();
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
	public static HttpRespBean httpRequest(HttpReqBean reqBean, CloseableHttpClient httpClient, CookieStore cookieStore) {
		Integer type = reqBean.getType();
		if (type == null || type!=1) reqBean.setType(0);
		if (Util.isBlank(reqBean.getUrl())) return new HttpRespBean(-999, "缺少参数");
		if (reqBean.getType() == 0) {
			return doGet(reqBean, httpClient, cookieStore);
		} else {
			return doPost(reqBean, httpClient, cookieStore);
		}
	}
	public static HttpRespBean doGet(HttpReqBean reqBean, CloseableHttpClient httpClient, CookieStore cookieStore) {
		String url = reqBean.getUrl();
		Map<String,Object> parammap = reqBean.getParams();
		List<Cookie> cookies = reqBean.getCookies();
		LogUtil.info(HttpClientUtil.class, "httpRequest url:" + url + ";param:" + JsonUtil.beanToJson(parammap));
		StringBuffer url_final = new StringBuffer(url);
		if (url.indexOf("?") == -1 && !Util.isBlank(reqBean.getParamStr())) {
			url_final.append("?");
		}
		if (cookies != null && !cookies.isEmpty() && cookieStore != null) {
			for (Cookie cookie : cookies)
				cookieStore.addCookie(cookie);
		}
		String paramStr = reqBean.getParamStr();
		if (!Util.isBlank(paramStr)) url_final.append(paramStr);
		LogUtil.info(HttpClientUtil.class, "doget reqBean url..." + url_final);
		HttpGet httpGet = new HttpGet(url_final.toString());
		try (CloseableHttpResponse resp = httpClient.execute(httpGet)) {
			return respToBean(resp, cookieStore);
		} catch (Exception e) {
			LogUtil.error(HttpClientUtil.class, e, "doget respBean error");
			return new HttpRespBean(-998, "系统发生异常：" + e.getMessage());
		}

	}
	public static HttpRespBean doPost(HttpReqBean reqBean, CloseableHttpClient httpClient, CookieStore cookieStore) {
		String url = reqBean.getUrl();
		Map<String,Object> parammap = reqBean.getParams();
		List<Cookie> cookies = reqBean.getCookies();
		LogUtil.info(HttpClientUtil.class, "httpRequest url:" + url + ";param:" + JsonUtil.beanToJson(parammap));
		HttpPost post = new HttpPost(url);
		if (cookies != null && !cookies.isEmpty()&& cookieStore != null) {
			for (Cookie cookie : cookies)
				cookieStore.addCookie(cookie);
		}
		List<NameValuePair> list = reqBean.getParamList();
		if (list != null && !list.isEmpty())
			try {
				post.setEntity(new UrlEncodedFormEntity(list));
			} catch (Exception e) {
				LogUtil.error(HttpClientUtil.class, e, "post.setEntity error");
			}
		try(CloseableHttpResponse response = httpClient.execute(post)) {
			return respToBean(response, cookieStore);
		} catch (Exception e) {
			LogUtil.error(HttpClientUtil.class, e, "doPost 发生异常");
			return new HttpRespBean(-998, "系统发生异常：" + e.getMessage());
		}
	}
	public static ResultInfo getStaticToFile(String url, String fileName) throws Exception {
		if (Util.isBlank(url)) return null;
		CookieStore cookieStore = new BasicCookieStore();
		ResultInfo ri = new ResultInfo(1, "success");
		CloseableHttpClient httpclient = HttpClients.custom()
				.setDefaultCookieStore(cookieStore)
				.build();
		try {
			StringBuffer url_final = new StringBuffer(url);
			LogUtil.info(HttpClientUtil.class, "doget url:" + url_final.toString());
			HttpGet httpGet = new HttpGet(url_final.toString());
			System.out.println(url_final.toString());
			InputStream is = null;
			CloseableHttpResponse response = httpclient.execute(httpGet);
			try {
				System.out.println(response.getStatusLine());
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				FileUtil.inputstreamtofile(is, fileName);
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

	private static HttpRespBean respToBean(HttpResponse httpResponse, CookieStore cookieStore) {
		if (httpResponse == null) return new HttpRespBean(-1, "httpResponse 为空");
		Integer statusLine = httpResponse.getStatusLine().getStatusCode();
		if (statusLine>400) return new HttpRespBean(-2, "reqErr:"+statusLine);
		HttpRespBean bean = new HttpRespBean();
		bean.setCodeAndMsg(1, "success:" + statusLine);
		HttpEntity entity = httpResponse.getEntity();
		bean.setResponseBody(entityToString(entity));
		bean.setHeaders(Arrays.asList(httpResponse.getAllHeaders()));
		if (cookieStore != null) {
			bean.setCookies(cookieStore.getCookies());
		}
		return bean;
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
}

//data = {
//		"source": "group",
//		"redir": "https://www.douban.com/group/",
//		"form_email": "xs94xs@sina.com",
//		"form_password": "wdmmwrz123",
//		"captcha-id": captchaid,
//		"captcha-solution": captchasolution,
//		# "remember": "on",
//		"login": u"登录"
//		}