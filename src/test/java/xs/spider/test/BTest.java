package xs.spider.test;

import org.junit.Test;

/**
 * Created by xs on 2017/4/8.
 */
public class BTest {
    @Test
    public void test() {
        ClassLoader classLoader = getClass().getClassLoader();
        Integer a = 1;
        Integer b=2;
        Integer c=3;
        Integer d=3;
        Integer h = 318;
        Integer e=321;
        Integer f=321;
        Long g=3L;
        System.out.println(c==d);
        System.out.println(e==f);
        System.out.println(c.equals(a+b));
        System.out.println(c==(a+b));
        System.out.println(g == (a+b));
        System.out.println(g.equals(a+b));
        System.out.println(e==(h+c));
    }
}
