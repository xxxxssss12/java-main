package xs.spider.base.util;

import java.io.*;

/**
 * Created by xs on 2017/4/4.
 */
public class FileUtil {
    public static void inputstreamtofile(InputStream ins, String filename){
        OutputStream os = null;
        try {
            File file = new File(filename);

            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            LogUtil.error(FileUtil.class,e, "inputstreamtofile fail");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
