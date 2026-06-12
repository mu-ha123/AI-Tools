package com.ai.toolbox.interfaces.controller;

import com.ai.toolbox.application.worklog.WorklogAppService;
import com.ai.toolbox.application.worklog.dto.WorkCategoryDTO;
import com.ai.toolbox.application.worklog.dto.WorkRecordDTO;
import com.ai.toolbox.common.result.Result;
import com.ai.toolbox.interfaces.util.ExcelExportUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/work")
@RequiredArgsConstructor
public class WorklogController {

    private final WorklogAppService worklogAppService;

    @GetMapping("/categories")
    public Result<List<WorkCategoryDTO>> listCategories() {
        return Result.success(worklogAppService.listCategories());
    }

    @PostMapping("/categories")
    public Result<WorkCategoryDTO> createCategory(@RequestBody Map<String, String> body) {
        return Result.success(worklogAppService.createCategory(body.get("name"), body.get("color")));
    }

    @PutMapping("/categories/{id}")
    public Result<WorkCategoryDTO> updateCategory(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return Result.success(worklogAppService.updateCategory(id, body.get("name"), body.get("color")));
    }

    @DeleteMapping("/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        worklogAppService.deleteCategory(id);
        return Result.success();
    }

    @GetMapping("/records")
    public Result<List<WorkRecordDTO>> listRecords(
            @RequestParam(value = "viewType", defaultValue = "DAY") String viewType,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(value = "categoryId", required = false) Long categoryId) {
        if (start != null && end != null) {
            return Result.success(worklogAppService.listRecordsByRange(start, end, categoryId));
        }
        return Result.success(worklogAppService.listRecords(viewType, date, categoryId));
    }

    @PostMapping("/records")
    public Result<WorkRecordDTO> createRecord(@RequestBody Map<String, Object> body) {
        Long categoryId = Long.valueOf(body.get("categoryId").toString());
        String title = (String) body.get("title");
        String description = (String) body.get("description");
        LocalDate recordDate = LocalDate.parse((String) body.get("recordDate"));
        LocalDate endDate = body.containsKey("endDate") ? LocalDate.parse((String) body.get("endDate")) : null;
        String dateType = (String) body.get("dateType");
        return Result.success(worklogAppService.createRecord(categoryId, title, description, recordDate, endDate, dateType));
    }

    @PutMapping("/records/{id}")
    public Result<WorkRecordDTO> updateRecord(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long categoryId = body.containsKey("categoryId") ? Long.valueOf(body.get("categoryId").toString()) : null;
        String title = (String) body.get("title");
        String description = (String) body.get("description");
        LocalDate recordDate = body.containsKey("recordDate") ? LocalDate.parse((String) body.get("recordDate")) : null;
        LocalDate endDate = body.containsKey("endDate") ? LocalDate.parse((String) body.get("endDate")) : null;
        String dateType = (String) body.get("dateType");
        return Result.success(worklogAppService.updateRecord(id, categoryId, title, description, recordDate, endDate, dateType));
    }

    @DeleteMapping("/records/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        worklogAppService.deleteRecord(id);
        return Result.success();
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportRecords(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) throws IOException {
        List<WorkRecordDTO> records = worklogAppService.exportRecords(start, end);
        byte[] excelBytes = ExcelExportUtil.exportWorkRecords(records);

        String filename = "工作输出记录_" + start + "_" + end + ".xlsx";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.ok().headers(headers).body(excelBytes);
    }
}
