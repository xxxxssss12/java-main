package xs.spider.work.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xs.spider.work.bean.SubwayLine;
import xs.spider.work.bean.SubwayLineStation;
import xs.spider.work.bean.SubwayStation;
import xs.spider.work.dao.SubwayLineDao;
import xs.spider.work.dao.SubwayLineStationDao;
import xs.spider.work.dao.SubwayStationDao;
import xs.spider.work.util.SubwayUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by xs on 2017/9/24
 */
@Service
public class SubwayService {
    @Autowired
    private SubwayLineDao subwayLineDao;

    @Autowired
    private SubwayLineStationDao subwayLineStationDao;

    @Autowired
    private SubwayStationDao subwayStationDao;
    public SubwayStation getStationById(Integer stationId) {
        return subwayStationDao.get(stationId);
    }
    public void importStationAndLineInfo() throws Exception {
        Map<String,List<String>> map =  SubwayUtil.getLineStationMap();
        if (map == null) return;
        for (String lineStr : map.keySet()) {
            SubwayLine line = new SubwayLine();
            line.setName(lineStr);
            line.setCreateTime(new Date());
            Integer lineId = subwayLineDao.save(line);
            for (String stationStr : map.get(lineStr)) {
                SubwayStation station = new SubwayStation();
                station.setName(stationStr);
                SubwayStation _station = subwayStationDao.get(station);
                if (_station == null) {
                    _station = station;
                    _station.setCreateTime(new Date());
                    subwayStationDao.save(_station);
                }
                SubwayLineStation sls = new SubwayLineStation();
                sls.setLineId(lineId);
                sls.setStationId(_station.getId());
                subwayLineStationDao.save(sls);
            }
        }
    }
}
