package xs.spider.work.service;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xs.spider.base.bean.ResultInfo;
import xs.spider.base.util.DateUtil;
import xs.spider.base.util.ExceptionWrite;
import xs.spider.base.util.LogUtil;
import xs.spider.base.util.Util;
import xs.spider.work.bean.ModelAll;
import xs.spider.work.bean.ModelDetail;
import xs.spider.work.bean.ModelType;
import xs.spider.work.dao.ModelAllDao;
import xs.spider.work.dao.ModelDetailDao;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
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
    @Autowired
    private ModelTypeServiceImpl modelTypeService;
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

                for (int i=1; i<=rowNum; i++) {
                    HSSFRow row = sheet.getRow(i);
                    int maxColNum = row.getPhysicalNumberOfCells();
                    ResultInfo ri = buildBean(row, maxColNum);
                    if (ri.getCode() < 0) {
                        if (ri.getCode() == -999) {
                            System.out.println(((JSONObject) ri.getData()).getString("modelNo") + "");
                        }
                    } else {
                        ModelAll modelAll = ((JSONObject) ri.getData()).getObject("modelAll", ModelAll.class);
                        ModelDetail modelDetail = ((JSONObject) ri.getData()).getObject("modelDetail", ModelDetail.class);

                        Integer id = modelAllDao.save(modelAll);
                        modelDetail.setModelAllId(id);
                        modelDetailDao.save(modelDetail);
                    }
//                    for (int j=0; j<=maxColNum; j++) {
//                        sb.append(getCellFormatValue(row.getCell((short) j)).trim()).append("||");
//                    }
//                    sb.append("\r\n");
                }
            }catch (Exception e) {
                log.error(ExceptionWrite.get(e));
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            log.error(ExceptionWrite.get(e));
        }
    }

    private ResultInfo buildBean(HSSFRow row, int maxColNum) {
        ModelAll modelAll = new ModelAll();
        ModelDetail modelDetail = new ModelDetail();
        JSONObject obj = new JSONObject();
        obj.put("modelAll", modelAll);
        obj.put("modelDetail", modelDetail);
        obj.put("modelNo", getCellFormatValue(row.getCell(0)).trim());
        String modelNo = getCellFormatValue(row.getCell(0)).trim();
        if (Util.isBlank(modelNo)) return ResultInfo.buildFail("");
        modelAll.setModelNo(modelNo);

        String name = getCellFormatValue(row.getCell(1)).trim();
        modelAll.setName(Util.isBlank(name)?null:name);

        String lingjiancaizhi = getCellFormatValue(row.getCell(2)).trim();
        modelAll.setLingjiancaizhi(Util.isBlank(lingjiancaizhi)?null:lingjiancaizhi);

        String modelDetailStr = getCellFormatValue(row.getCell(3)).trim();
        modelDetail.setDetail(Util.isBlank(modelDetailStr)?null:modelDetailStr);

        ResultInfo dateDealResult = dealDate(row.getCell(4));
        String date = getCellFormatValue(row.getCell(4)).trim();
        if (dateDealResult.getCode() < 0) {
//            if (!date.contains("/"))
                return ResultInfo.buildFail(-999, "fail", obj);
//            else modelAll.setCreateTime(DateUtil.parseStringToDate(date, DateUtil.C_YYYY_MM_DD_HH_MM_SS));
        } else if (dateDealResult.getCode() == 1) {
            modelAll.setCreateTime((Date) dateDealResult.getData());
        }

        String nowLocation = getCellFormatValue(row.getCell(5)).trim();
        modelAll.setStoreLocation(Util.isBlank(nowLocation) ? null : nowLocation);

        String remark = getCellFormatValue(row.getCell(6)).trim();
        modelAll.setRemark(Util.isBlank(remark) ? null:remark);

        String modelTypeName = getCellFormatValue(row.getCell(7)).trim();
        modelAll.setModelTypeName(Util.isBlank(modelTypeName) ? null : modelTypeName);

        String createLocal = getCellFormatValue(row.getCell(8)).trim();
        modelAll.setCreateLocation(Util.isBlank(createLocal) ? null : createLocal);

        ModelType type = new ModelType();
        type.setName(modelTypeName);
        ModelType type_ = modelTypeService.get(type);
        if (type_ == null) {
            type.setCreateTime(new Date());
            modelAll.setModelTypeId(modelTypeService.save(type));
        } else {
            modelAll.setModelTypeId(type_.getId());
        }
        return ResultInfo.build(obj);
    }

    private ResultInfo dealDate(HSSFCell cell) {
        String strCell = null;
        if (cell == null) return ResultInfo.build(2, "success");
        switch (cell.getCellTypeEnum()) {
            case STRING:
                strCell = cell.getStringCellValue();
                break;
            case NUMERIC:
                double d = cell.getNumericCellValue();
                strCell = String.valueOf(d);
                break;
            default:
                break;
        }
        try {
            if (Util.isBlank(Util.null2string(strCell).trim())) return ResultInfo.build(2, "success");
            Integer dateNum = new BigDecimal(strCell).intValue();
            return ResultInfo.build(DateUtil.addDays(firstDate, dateNum-firstDateDay));
        } catch (Exception e) {
//            log.error(strCell + ":" + ExceptionWrite.get(e));
            return ResultInfo.buildFail(-1, "formatError");
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
