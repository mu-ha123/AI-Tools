package com.ai.toolbox.interfaces.controller;

import com.ai.toolbox.application.overtime.OvertimeAppService;
import com.ai.toolbox.application.overtime.dto.AttendanceRecordDTO;
import com.ai.toolbox.application.overtime.dto.MonthlySummaryDTO;
import com.ai.toolbox.application.overtime.dto.OvertimeSettingsDTO;
import com.ai.toolbox.common.result.Result;
import com.ai.toolbox.infrastructure.ai.OvertimeAiService;
import com.ai.toolbox.common.result.ErrorCode;
import com.ai.toolbox.common.exception.BizException;
import com.ai.toolbox.interfaces.dto.AiAnalyzeRequest;
import com.ai.toolbox.interfaces.dto.AiParseRequest;
import com.ai.toolbox.interfaces.dto.AttendanceRecordRequest;
import com.ai.toolbox.interfaces.dto.OvertimeSettingsRequest;
import com.ai.toolbox.interfaces.util.ExcelImportUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/overtime")
@RequiredArgsConstructor
public class OvertimeController {

    private final OvertimeAppService overtimeAppService;
    private final OvertimeAiService overtimeAiService;

    @PostMapping("/settings")
    public Result<OvertimeSettingsDTO> saveSettings(@Valid @RequestBody OvertimeSettingsRequest request) {
        OvertimeSettingsDTO settings = overtimeAppService.saveSettings(
                request.getWorkStartTime(),
                request.getWorkEndTime(),
                request.getLunchStartTime(),
                request.getLunchEndTime(),
                request.getStandardWorkMinutes(),
                request.getCalculationMode());
        return Result.success(settings);
    }

    @GetMapping("/settings")
    public Result<OvertimeSettingsDTO> getSettings() {
        return Result.success(overtimeAppService.getSettings());
    }

    @PostMapping("/records/leave")
    public Result<AttendanceRecordDTO> toggleLeave(@RequestBody Map<String, String> body) {
        LocalDate workDate = LocalDate.parse(body.get("workDate"));
        AttendanceRecordDTO record = overtimeAppService.toggleLeave(workDate);
        if (record == null) {
            return Result.success();
        }
        return Result.success(record);
    }

    @PostMapping("/import")
    public Result<Map<String, Object>> importExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BizException(ErrorCode.OVERTIME_IMPORT_ERROR, "上传文件为空");
        }
        List<ExcelImportUtil.ExcelRow> rows;
        try {
            rows = ExcelImportUtil.parse(file.getInputStream());
        } catch (IOException e) {
            throw new BizException(ErrorCode.OVERTIME_IMPORT_ERROR, "文件读取失败");
        }
        if (rows.isEmpty()) {
            throw new BizException(ErrorCode.OVERTIME_IMPORT_ERROR, "未找到有效数据");
        }
        List<LocalDate> dates = rows.stream().map(ExcelImportUtil.ExcelRow::date).collect(Collectors.toList());
        List<LocalTime> clockIns = rows.stream().map(ExcelImportUtil.ExcelRow::clockIn).collect(Collectors.toList());
        List<LocalTime> clockOuts = rows.stream().map(ExcelImportUtil.ExcelRow::clockOut).collect(Collectors.toList());
        List<AttendanceRecordDTO> records = overtimeAppService.importRecords(dates, clockIns, clockOuts);
        Map<String, Object> result = new HashMap<>();
        result.put("total", records.size());
        result.put("records", records);
        return Result.success(result);
    }

    @PostMapping("/records")
    public Result<AttendanceRecordDTO> saveRecord(@Valid @RequestBody AttendanceRecordRequest request) {
        AttendanceRecordDTO record = overtimeAppService.saveRecord(
                request.getWorkDate(), request.getClockIn(), request.getClockOut());
        return Result.success(record);
    }

    @GetMapping("/summary")
    public Result<MonthlySummaryDTO> getSummary(@RequestParam("year") int year, @RequestParam("month") int month) {
        return Result.success(overtimeAppService.getMonthlySummary(year, month));
    }

    @PostMapping("/ai/parse")
    public Result<Map<String, Object>> parseByAi(@Valid @RequestBody AiParseRequest request) {
        Map<String, String> parsed = overtimeAiService.parseAttendance(request.getText());
        OvertimeAiService.ParsedAttendance attendance = overtimeAiService.toParsedAttendance(parsed);
        Map<String, Object> response = new HashMap<>();
        response.put("workDate", attendance.workDate().toString());
        response.put("clockIn", attendance.clockIn() != null ? attendance.clockIn().toString() : "");
        response.put("clockOut", attendance.clockOut() != null ? attendance.clockOut().toString() : "");
        return Result.success(response);
    }

    @PostMapping("/ai/analyze")
    public Result<String> analyzeByAi(@Valid @RequestBody AiAnalyzeRequest request) {
        MonthlySummaryDTO summary = overtimeAppService.getMonthlySummary(request.getYear(), request.getMonth());
        String context = buildSummaryContext(summary);
        return Result.success(overtimeAiService.analyzeSummary(context));
    }

    private String buildSummaryContext(MonthlySummaryDTO summary) {
        StringBuilder builder = new StringBuilder();
        builder.append("骞翠唤:").append(summary.getYear())
                .append(",鏈堜唤:").append(summary.getMonth())
                .append(",鎬诲姞鐝垎閽?").append(summary.getTotalOvertimeMinutes())
                .append(",鎬诲姞鐝?").append(summary.getTotalOvertimeText())
                .append("\n姣忔棩鏄庣粏:\n");
        summary.getDailyRecords().forEach(record -> builder.append(record.getWorkDate())
                .append(" 涓婄彮").append(record.getClockIn())
                .append(" 涓嬬彮").append(record.getClockOut())
                .append(" 鍔犵彮").append(record.getOvertimeMinutes()).append("鍒嗛挓\n"));
        return builder.toString();
    }
}
