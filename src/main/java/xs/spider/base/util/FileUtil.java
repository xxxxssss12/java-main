package xs.spider.base.util;

import java.io.*;

/**
 * Created by xs on 2017/4/4.
 */
public class FileUtil {
    public static void inputstreamtofile(InputStream ins, String filename){
        OutputStream os = null;
        try {
            String path = getFilePath(filename);
            File pathFile = new File(path);
            if (!pathFile.isDirectory()) pathFile.mkdirs();
            File file = new File(filename);
            file.createNewFile();
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

    public static String getFilePath(String filename) {
        if (filename.endsWith("/") || filename.endsWith("\\")) return filename;
        Integer index = filename.lastIndexOf("\\");
        Integer index2 = filename.lastIndexOf("/");
        if (index < index2) index = index2;
        if (index < 0) return null;
        return filename.substring(0, index);
    }
}
