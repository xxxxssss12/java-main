package xs.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HttpTest {

    // 假设你已经获取到了视频链接数组
    public static List<String> videoLinks = new ArrayList<>();

    static {
        String[] str = new String[]{
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/ld/e/6/t/1/433924315785.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
        "https://cloud.video.taobao.com/play/u/33548509/p/1/d/ld/e/6/t/1/433048412589.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/ld/e/6/t/1/432904480428.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/ld/e/6/t/1/433845091030.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/ld/e/6/t/1/433495534643.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/ld/e/6/t/1/433222465700.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/ld/e/6/t/1/433845091031.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/ld/e/6/t/1/433917759278.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/ld/e/6/t/1/432726444855.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/ld/e/6/t/1/432905740554.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/ld/e/6/t/1/433483702772.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/ld/e/6/t/1/433463338089.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/ld/e/6/t/1/432904480429.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/ld/e/6/t/1/433181337464.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/ld/e/6/t/1/432791844357.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/ld/e/6/t/1/432904480430.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/ld/e/6/t/1/433495534645.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/sd_265/e/6/t/1/478391083338.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/ld/e/6/t/1/477981768281.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/hd_265/e/6/t/1/477930544291.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ=",
                "https://cloud.video.taobao.com/play/u/33548509/p/1/d/hd_265/e/6/t/1/478411787677.mp4?auth_key=YXBwX2tleT04MDAwMDAwMTImYXV0aF9pbmZvPXsidGltZXN0YW1wRW5jcnlwdGVkIjoiODU3ZDE3ZjAzMjU2NDMzYjk2MDljYWU5NjU3MjcwZmUifSZkdXJhdGlvbj0mdGltZXN0YW1wPTE3Mzc0NzY5NTQ="

        };
        Arrays.stream(str).forEach(videoLinks::add);
    }

    public static void main(String[] args) {
        // 设置下载的文件保存路径
        String saveDir = "./downloaded_videos";
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdirs();  // 如果文件夹不存在则创建
        }

        // 下载视频并命名
        for (int i = 0; i < videoLinks.size(); i++) {
            String link = videoLinks.get(i);
            String filename = String.format("%02d.mp4", i + 1); // 两位数字命名
            String filePath = saveDir + "/" + filename;

            try {
                // 下载视频
                downloadFile(link, filePath);
                System.out.println("视频 " + filename + " 下载完成！");
            } catch (IOException e) {
                System.out.println("下载 " + filename + " 时出错: " + e.getMessage());
            }
        }

        System.out.println("所有视频下载完成！");
    }

    // 下载文件的函数
    public static void downloadFile(String fileURL, String savePath) throws IOException {
        // 创建 URL 对象
        URL url = new URL(fileURL);

        // 打开连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);  // 设置连接超时
        connection.setReadTimeout(5000);     // 设置读取超时

        // 获取输入流
        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(savePath)) {

            byte[] buffer = new byte[4096];  // 设置缓存区
            int bytesRead;

            // 读取并写入文件
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        // 关闭连接
        connection.disconnect();
    }
}
