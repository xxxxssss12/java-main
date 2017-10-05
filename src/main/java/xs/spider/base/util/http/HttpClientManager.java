package xs.spider.base.util.http;

import org.apache.http.client.HttpClient;

/**
 * Created by xs on 2017/9/30.
 */
public class HttpClientManager {
    private static ThreadLocal<HttpClient> httpClientThreadLocal = new ThreadLocal<>();
    public static HttpClient getHttpClient() {
        return httpClientThreadLocal.get();
    }

    public static void setHttpClient(HttpClient httpClient) {
        if (httpClient != null)
            httpClientThreadLocal.set(httpClient);
    }
}
