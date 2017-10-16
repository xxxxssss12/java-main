package xs.spider.work.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xs.spider.work.bean.Menu;
import xs.spider.work.bean.User;
import xs.spider.work.dao.MenuDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xs on 2017/10/15
 */
@Service
public class MenuServiceImpl {
    @Autowired
    private MenuDao menuDao;

    public JSONArray findUserMenuTree(User user) {
        if (user == null) return null;
        if (user.getRoleId() == null) return null;
        List<Menu> originalList = menuDao.getListByRoleId(user.getRoleId());
        if (originalList == null || originalList.isEmpty()) return null;
        Map<String, Integer> menuListIndex = new HashMap<>();
        JSONArray result = new JSONArray();
        for (Menu menu : originalList) {
            if (menu.getParentId() == -1) {
                menuListIndex.put(menu.getId().toString(), result.size());
                result.add(buildJsonObj(menu));
            }
        }
        for (Menu menu : originalList) {
            if (menu.getParentId() == -1) continue;
            Integer parentIdIndex = menuListIndex.get(menu.getParentId().toString());
            JSONObject obj = result.getJSONObject(parentIdIndex);
            JSONArray childMenu = obj.getJSONArray("childMenu");
            childMenu.add(buildJsonObj(menu));
        }
        return result;
    }

    private JSONObject buildJsonObj(Menu menu) {
        JSONObject obj = new JSONObject();
        obj.put("id", menu.getId());
        obj.put("url", menu.getUrl());
        obj.put("name", menu.getName());
        if (menu.getParentId() == -1) {
            obj.put("childMenu", new JSONArray());
        }
        return obj;
    }
}
