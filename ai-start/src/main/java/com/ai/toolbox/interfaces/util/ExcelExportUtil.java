package com.ai.toolbox.interfaces.util;

import com.ai.toolbox.application.worklog.dto.WorkRecordDTO;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExportUtil {

    private static final String[] HEADERS = {"开始日期", "结束日期", "粒度", "分类", "标题", "工作内容"};

    public static byte[] exportWorkRecords(List<WorkRecordDTO> records) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("工作输出记录");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle normalStyle = createNormalStyle(workbook);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            sheet.setColumnWidth(0, 14 * 256);
            sheet.setColumnWidth(1, 14 * 256);
            sheet.setColumnWidth(2, 8 * 256);
            sheet.setColumnWidth(3, 14 * 256);
            sheet.setColumnWidth(4, 40 * 256);
            sheet.setColumnWidth(5, 60 * 256);

            int rowNum = 1;
            for (WorkRecordDTO record : records) {
                Row row = sheet.createRow(rowNum++);

                Cell startCell = row.createCell(0);
                startCell.setCellValue(record.getRecordDate().toString());
                startCell.setCellStyle(dateStyle);

                Cell endCell = row.createCell(1);
                endCell.setCellValue(record.getEndDate() != null ? record.getEndDate().toString() : record.getRecordDate().toString());
                endCell.setCellStyle(dateStyle);

                Cell typeCell = row.createCell(2);
                String typeLabel;
                switch (record.getDateType()) {
                    case "WEEK": typeLabel = "周"; break;
                    case "MONTH": typeLabel = "月"; break;
                    default: typeLabel = "日";
                }
                typeCell.setCellValue(typeLabel);
                typeCell.setCellStyle(normalStyle);

                Cell catCell = row.createCell(3);
                catCell.setCellValue(record.getCategoryName());
                catCell.setCellStyle(normalStyle);

                Cell titleCell = row.createCell(4);
                titleCell.setCellValue(record.getTitle());
                titleCell.setCellStyle(normalStyle);

                Cell descCell = row.createCell(5);
                descCell.setCellValue(record.getDescription() != null ? record.getDescription() : "");
                descCell.setCellStyle(normalStyle);
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }

    private static CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private static CellStyle createDateStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private static CellStyle createNormalStyle(Workbook wb) {
        return wb.createCellStyle();
    }
}
