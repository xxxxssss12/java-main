package xs.spider.base.util;

import java.security.MessageDigest;

/**
 * MD5加密类。加密后写进数据库
 * 
 * @author xs
 */
public class MD5Util {
    public final static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            LogUtil.error(MD5Util.class, e);
            return null;
        }
    }
    public final static String MD5x5(String s) {
        return MD5xN(s, 5);
    }

    private final static String MD5xN(String s, int n) {
        if (n<1) return null;
        String md = s;
        for (int i=0; i<n; i++) {
            md = MD5(md);
        }
        return md;
    }
}
