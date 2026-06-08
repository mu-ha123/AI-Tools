package com.ai.toolbox.interfaces.util;

import com.ai.toolbox.common.result.ErrorCode;
import com.ai.toolbox.common.exception.BizException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ExcelImportUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static List<ExcelRow> parse(InputStream inputStream) {
        List<ExcelRow> rows = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                LocalDate date = parseDateCell(row.getCell(0), i);
                LocalTime clockIn = parseTimeCell(row.getCell(1), i);
                LocalTime clockOut = parseTimeCell(row.getCell(2), i);

                if (date == null || clockIn == null || clockOut == null) continue;

                rows.add(new ExcelRow(date, clockIn, clockOut));
            }
        } catch (IOException e) {
            throw new BizException(ErrorCode.OVERTIME_IMPORT_ERROR, "文件读取失败: " + e.getMessage());
        }
        return rows;
    }

    private static LocalDate parseDateCell(Cell cell, int rowNum) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            }
            String value = getStringValue(cell);
            if (value == null || value.isBlank()) return null;
            return LocalDate.parse(value.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BizException(ErrorCode.OVERTIME_IMPORT_ERROR, "第" + (rowNum + 1) + "行日期格式错误");
        }
    }

    private static LocalTime parseTimeCell(Cell cell, int rowNum) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalTime();
            }
            if (cell.getCellType() == CellType.NUMERIC) {
                double numericValue = cell.getNumericCellValue();
                int totalMinutes = (int) (numericValue * 24 * 60);
                int hours = totalMinutes / 60;
                int minutes = totalMinutes % 60;
                return LocalTime.of(hours, minutes);
            }
            String value = getStringValue(cell);
            if (value == null || value.isBlank()) return null;
            value = value.trim();
            if (value.length() <= 5) {
                return LocalTime.parse(value, TIME_FORMATTER);
            }
            return LocalTime.parse(value.substring(0, 5), TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BizException(ErrorCode.OVERTIME_IMPORT_ERROR, "第" + (rowNum + 1) + "行时间格式错误");
        }
    }

    private static String getStringValue(Cell cell) {
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            double val = cell.getNumericCellValue();
            if (val == Math.floor(val) && !Double.isInfinite(val)) {
                return String.valueOf((long) val);
            }
            return String.valueOf(val);
        }
        return null;
    }

    public static List<GlossaryExcelRow> parseGlossary(InputStream inputStream) {
        List<GlossaryExcelRow> rows = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String seq = getStringValue(row.getCell(0));
                String name = getStringValue(row.getCell(1));
                String desc = getStringValue(row.getCell(2));

                if (name == null || name.isBlank()) continue;

                rows.add(new GlossaryExcelRow(seq, name.trim(), desc != null ? desc.trim() : ""));
            }
        } catch (IOException e) {
            throw new BizException(ErrorCode.OVERTIME_IMPORT_ERROR, "文件读取失败: " + e.getMessage());
        }
        return rows;
    }

    public record ExcelRow(LocalDate date, LocalTime clockIn, LocalTime clockOut) {}

    public record GlossaryExcelRow(String seq, String name, String description) {}
}
