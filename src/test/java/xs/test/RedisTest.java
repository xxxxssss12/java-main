package xs.test;

import xs.spider.base.util.RedisConnector;

/**
 * Created by xs on 2017/8/31
 */
public class RedisTest {

    public static void createTime() {
        Long timestamp = System.currentTimeMillis();
        long sequence = RedisConnector.incr("test");
        String seq = String.format("%03d", sequence % 1000);
        if (RedisConnector.setnx("test:" + seq + timestamp) <= 0) {
            System.out.println("test:" + seq + timestamp + "已存在！！！incr=" +sequence);
        }
        if (sequence % 10000 == 0) System.out.println(sequence);
    }
    public static void main(String[] args) {
        for (int i=0; i<30; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j=0; j<100000; j++) {
                        createTime();
                    }
                }
            }).start();
        }
    }
}
