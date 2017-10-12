package xs.spider.work.service;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xs.spider.base.util.DateUtil;
import xs.spider.base.util.ExceptionWrite;
import xs.spider.base.util.LogUtil;
import xs.spider.work.dao.ModelAllDao;
import xs.spider.work.dao.ModelDetailDao;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xs on 2017/10/5.
 */
@Service
public class CBServiceImpl {
    private Logger log = LogUtil.getLogger(getClass());
    private static final int firstDateDay = 25569;
    private static final Date firstDate = new Date(0);
    @Autowired
    private ModelAllDao modelAllDao;
    @Autowired
    private ModelDetailDao modelDetailDao;
    public void importOnXls(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            log.info("文件不存在");
        }
        StringBuilder sb = new StringBuilder();
        try(FileInputStream fis = new FileInputStream(file)) {
            try (HSSFWorkbook wb = new HSSFWorkbook(fis)) {
                HSSFSheet sheet = wb.getSheetAt(0);
                int rowNum = sheet.getLastRowNum();

                for (int i=1; i<rowNum; i++) {
                    HSSFRow row = sheet.getRow(i);
                    int colNum = row.getPhysicalNumberOfCells();
                    for (int j=0; j<colNum; j++) {
                        sb.append(getCellFormatValue(row.getCell((short) j)).trim()).append("    ");
                    }
                    sb.append("\r\n");
                }
            }catch (Exception e) {
                log.error(ExceptionWrite.get(e));
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            log.error(ExceptionWrite.get(e));
        }
    }

    private String getCellFormatValue(HSSFCell cell) {
        String strCell = null;
        if (cell == null) {
            return "";
        }
        switch (cell.getCellTypeEnum()) {
            case STRING:
                strCell = cell.getStringCellValue();
                break;
            case NUMERIC:
                double d = cell.getNumericCellValue();
                if (d>= firstDateDay) {
                    strCell = DateUtil.formatDateTime(DateUtil.addDays(firstDate, (int)d-firstDateDay), 2);
                } else {
                    strCell = String.valueOf(cell.getNumericCellValue());
                }
//                if(HSSFDateUtil.isCellDateFormatted(cell)){
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                    strCell= sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue())).toString();
//                }else{
//                    strCell= NumberToTextConverter.toText(cell.getNumericCellValue());
//                    //System.out.println(">>>>>>>>>>>>>"+tookdate);
//                }
                break;
            case BOOLEAN:
                strCell = String.valueOf(cell.getBooleanCellValue());
                break;
            case BLANK:
                strCell = "";
                break;
            case FORMULA:
                strCell = String.valueOf(cell.getCellFormula());
                break;
            default:
                strCell = "";
                break;
        }
        return strCell;
    }

    public static void main(String[] args) {
        Date date = new Date(0);
        System.out.println(DateUtil.formatDateTime(date, 2));
        System.out.println(DateUtil.formatDateTime(DateUtil.addDays(date, 36586-25569), 2));
    }
}
