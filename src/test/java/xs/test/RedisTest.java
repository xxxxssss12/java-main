package xs.test;

import xs.spider.base.util.RedisConnector;

/**
 * Created by xs on 2017/8/31
 */
public class RedisTest {
    public static void main(String[] args) {
        System.out.println(RedisConnector.get("hehe"));
    }
}
