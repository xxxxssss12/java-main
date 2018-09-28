package xs.spider.work.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import xs.spider.base.dao.DaoSupport;
import xs.spider.base.dao.impl.DaoSupportImpl;
import xs.spider.work.bean.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xs on 2017/10/12
 */
@Component
public class UserDao extends DaoSupportImpl<User, Integer> implements DaoSupport<User, Integer> {
    public void insert() {
        NamedParameterJdbcTemplate template = getNamedJdbcTemplate();
        String sql = "INSERT INTO tmp values (:a)";
        File file = new File("/Users/xs/Documents/xs/fuck/real.txt");
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                try {
                    Map<String, Object> map = new HashMap<>();
                    map.put("a", s);
                    template.execute(sql, map, null);
                } catch (Exception e) {
                    System.out.println(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
