package xs.spider.work.util;

import com.alibaba.fastjson.JSON;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.util.Util;
import xs.spider.base.util.http.HttpClientUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xs on 2017/9/24
 */
public class SubwayUtil {
    public static Map<String, List<String>> getLineStationMap() throws IOException {
        String url = "http://bj.bendibao.com/ditie/linemap.shtml";
        Map<String, List<String>> lineStationMap = new LinkedHashMap<>();
        ResultInfo ri = HttpClientUtil.doGet(url, null);
        if (ri.getCode() != 1) return null;
        Document doc = Jsoup.parse((String) ri.getData());
        Elements list = doc.select(".line-list");
        for (Element ele : list) {
            String title = ele.select(".wrap").first()
                    .select("strong").first()
                    .select("a").first().text();
            if (Util.isBlank(title)) continue;
            title = title.replace("北京地铁", "").replace("线路图", "");
            List<String> stationNameList = new ArrayList<>();
            lineStationMap.put(title, stationNameList);
            Elements stationEles = ele.select(".line-list-station").first()
                    .select(".station");
            for (Element stationEle : stationEles) {
                stationNameList.add(stationEle.select(".link").first().text());
            }
        }
        return lineStationMap;
    }

}
