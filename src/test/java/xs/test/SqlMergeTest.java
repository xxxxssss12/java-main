package xs.test;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlMergeTest {


    public void generateUpdateStatements(String filePath) {

        Map<String, List<String>> siteMap = new HashMap<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String headerLine = br.readLine(); // 读取表头
            if (headerLine == null) {
                System.out.println("CSV 文件为空");
                return;
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = parseCsvLine(line);
                if (values.length < 10) continue; // 确保 site 和 case_id 存在

                String caseId = values[0].replaceAll("\"", ""); // case_id
                String site = values[9].replaceAll("\"", ""); // site

                if (site.startsWith("www.")) {
                    String updatedSite = "https://" + site;
                    addToMap(siteMap, updatedSite, caseId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        siteMap.forEach((updatedSite , caseIds ) -> {
            String caseIdList = String.join(",", caseIds);
            // 此处 table_name 请替换为实际的表名
            System.out.println("UPDATE table_name SET site = '" + updatedSite
                    + "' WHERE case_id IN (" + caseIdList + ");");

        });
    }

    private void addToMap(Map<String, List<String>> siteMap, String updatedSite, String caseId) {
        if (siteMap.get(updatedSite) == null) {
            siteMap.put(updatedSite, new ArrayList<>());
        }
        siteMap.get(updatedSite).add(caseId);
    }

    private String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder value = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(value.toString());
                value.setLength(0);
            } else {
                value.append(c);
            }
        }
        values.add(value.toString()); // 添加最后一个值

        return values.toArray(new String[0]);
    }

    @Test
    public void test() {
        String filePath = "E:\\Workspaces\\tmp\\1.csv";
        generateUpdateStatements(filePath);
    }
}
