package com.ai.toolbox.application.worklog;

import com.ai.toolbox.application.worklog.dto.WorkCategoryDTO;
import com.ai.toolbox.application.worklog.dto.WorkRecordDTO;
import com.ai.toolbox.application.worklog.dto.WorkSummaryDTO;
import com.ai.toolbox.common.exception.BizException;
import com.ai.toolbox.common.result.ErrorCode;
import com.ai.toolbox.domain.worklog.entity.WorkCategory;
import com.ai.toolbox.domain.worklog.entity.WorkRecord;
import com.ai.toolbox.domain.worklog.entity.WorkSummary;
import com.ai.toolbox.domain.worklog.repository.WorklogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorklogAppService {

    private final WorklogRepository worklogRepository;

    public List<WorkCategoryDTO> listCategories() {
        return worklogRepository.findAllCategories().stream()
                .map(this::toCategoryDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkCategoryDTO createCategory(String name, String color) {
        if (worklogRepository.findCategoryByName(name).isPresent()) {
            throw new BizException(ErrorCode.WORK_CATEGORY_DUPLICATE, "分类 '" + name + "' 已存在");
        }
        int maxOrder = worklogRepository.findAllCategories().size();
        WorkCategory saved = worklogRepository.saveCategory(WorkCategory.create(name, color, maxOrder + 1));
        return toCategoryDTO(saved);
    }

    @Transactional
    public WorkCategoryDTO updateCategory(Long id, String name, String color) {
        WorkCategory existing = worklogRepository.findCategoryById(id)
                .orElseThrow(() -> new BizException(ErrorCode.WORK_CATEGORY_NOT_FOUND));
        WorkCategory updated = WorkCategory.restore(id, name, color, existing.getSortOrder(), existing.isDefault());
        WorkCategory saved = worklogRepository.saveCategory(updated);
        return toCategoryDTO(saved);
    }

    @Transactional
    public void deleteCategory(Long id) {
        WorkCategory existing = worklogRepository.findCategoryById(id)
                .orElseThrow(() -> new BizException(ErrorCode.WORK_CATEGORY_NOT_FOUND));
        if (existing.isDefault()) {
            throw new BizException(ErrorCode.WORK_CATEGORY_DEFAULT_DELETE);
        }
        worklogRepository.deleteCategory(id);
    }

    public List<WorkRecordDTO> listRecords(String viewType, LocalDate date, Long categoryId, String status) {
        if (date == null) {
            date = LocalDate.now();
        }
        LocalDate start, end;
        switch (viewType != null ? viewType : "DAY") {
            case "WEEK":
                start = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                end = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                break;
            case "MONTH":
                start = date.with(TemporalAdjusters.firstDayOfMonth());
                end = date.with(TemporalAdjusters.lastDayOfMonth());
                break;
            default:
                start = date;
                end = date;
                break;
        }
        return queryRecordsInRange(start, end, categoryId, status);
    }

    public List<WorkRecordDTO> listRecordsByRange(LocalDate start, LocalDate end, Long categoryId, String status) {
        if (start == null) start = LocalDate.now().withDayOfMonth(1);
        if (end == null) end = LocalDate.now();
        return queryRecordsInRange(start, end, categoryId, status);
    }

    private List<WorkRecordDTO> queryRecordsInRange(LocalDate start, LocalDate end, Long categoryId, String status) {
        Map<Long, WorkCategory> categoryMap = worklogRepository.findAllCategories().stream()
                .collect(Collectors.toMap(WorkCategory::getId, c -> c));

        List<WorkRecord> records;
        if ("TODO".equals(status)) {
            records = worklogRepository.findRecordsByDateRangeAndStatus(start, end, status);
        } else {
            records = worklogRepository.findRecordsByDateRange(start, end);
        }
        return records.stream()
                .filter(r -> categoryId == null || r.getCategoryId().equals(categoryId))
                .filter(r -> "TODO".equals(status) || !"TODO".equals(r.getStatus()))
                .map(r -> toRecordDTO(r, categoryMap.get(r.getCategoryId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkRecordDTO createRecord(Long categoryId, String title, String description,
                                       LocalDate recordDate, LocalDate endDate, String dateType, String status) {
        if (worklogRepository.findCategoryById(categoryId).isEmpty()) {
            throw new BizException(ErrorCode.WORK_CATEGORY_NOT_FOUND);
        }
        if (endDate == null) {
            endDate = recordDate;
        }
        String dateTypeFinal = dateType != null ? dateType : "DAY";
        boolean isTodo = "TODO".equals(status);
        WorkRecord record = isTodo
                ? WorkRecord.createTodo(categoryId, title, description, recordDate, endDate, dateTypeFinal)
                : WorkRecord.create(categoryId, title, description, recordDate, endDate, dateTypeFinal);
        WorkRecord saved = worklogRepository.saveRecord(record);
        return toRecordDTO(saved, null);
    }

    @Transactional
    public WorkRecordDTO updateRecord(Long id, Long categoryId, String title, String description,
                                       LocalDate recordDate, LocalDate endDate, String dateType, String status) {
        WorkRecord existing = worklogRepository.findRecordById(id)
                .orElseThrow(() -> new BizException(ErrorCode.WORK_RECORD_NOT_FOUND));
        if (categoryId != null && worklogRepository.findCategoryById(categoryId).isEmpty()) {
            throw new BizException(ErrorCode.WORK_CATEGORY_NOT_FOUND);
        }
        Long finalCategoryId = categoryId != null ? categoryId : existing.getCategoryId();
        String finalTitle = title != null ? title : existing.getTitle();
        String finalDesc = description != null ? description : existing.getDescription();
        LocalDate finalStart = recordDate != null ? recordDate : existing.getRecordDate();
        LocalDate finalEnd = endDate != null ? endDate : existing.getEndDate();
        String finalDateType = dateType != null ? dateType : existing.getDateType();
        String finalStatus = status != null ? status : existing.getStatus();

        WorkRecord updated = WorkRecord.restore(id, finalCategoryId, finalTitle, finalDesc,
                finalStart, finalEnd, finalDateType, finalStatus, existing.getCreatedAt(), null);
        WorkRecord saved = worklogRepository.saveRecord(updated);
        return toRecordDTO(saved, null);
    }

    @Transactional
    public void deleteRecord(Long id) {
        if (worklogRepository.findRecordById(id).isEmpty()) {
            throw new BizException(ErrorCode.WORK_RECORD_NOT_FOUND);
        }
        worklogRepository.deleteRecord(id);
    }

    public List<WorkRecordDTO> exportRecords(LocalDate start, LocalDate end) {
        if (start == null) start = LocalDate.now().withDayOfMonth(1);
        if (end == null) end = LocalDate.now();
        return queryRecordsInRange(start, end, null, null);
    }

    @Transactional
    public WorkSummaryDTO generateSummary(LocalDate start, LocalDate end, String content) {
        worklogRepository.findSummaryByDateRange(start, end)
                .ifPresent(s -> { throw new BizException(ErrorCode.WORK_SUMMARY_DUPLICATE); });

        WorkSummary summary = WorkSummary.create(start, end, content);
        WorkSummary saved = worklogRepository.saveSummary(summary);
        return toSummaryDTO(saved);
    }

    public String buildSummaryContext(LocalDate start, LocalDate end) {
        Map<Long, WorkCategory> categoryMap = worklogRepository.findAllCategories().stream()
                .collect(Collectors.toMap(WorkCategory::getId, c -> c));

        List<WorkRecord> records = worklogRepository.findRecordsByDateRange(start, end);
        if (records.isEmpty()) {
            throw new BizException(ErrorCode.WORK_SUMMARY_NO_RECORDS);
        }

        return buildWorkContext(start, end, records, categoryMap);
    }

    public List<WorkSummaryDTO> listSummaries() {
        return worklogRepository.findAllSummaries().stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    public WorkSummaryDTO getSummary(Long id) {
        WorkSummary summary = worklogRepository.findSummaryById(id)
                .orElseThrow(() -> new BizException(ErrorCode.WORK_SUMMARY_NOT_FOUND));
        return toSummaryDTO(summary);
    }

    @Transactional
    public void deleteSummary(Long id) {
        if (worklogRepository.findSummaryById(id).isEmpty()) {
            throw new BizException(ErrorCode.WORK_SUMMARY_NOT_FOUND);
        }
        worklogRepository.deleteSummary(id);
    }

    private String buildWorkContext(LocalDate start, LocalDate end, List<WorkRecord> records, Map<Long, WorkCategory> categoryMap) {
        Map<String, List<WorkRecord>> grouped = records.stream()
                .collect(Collectors.groupingBy(
                        r -> {
                            WorkCategory c = categoryMap.get(r.getCategoryId());
                            return c != null ? c.getName() : "未分类";
                        },
                        LinkedHashMap::new,
                        Collectors.toList()));

        StringBuilder sb = new StringBuilder();
        sb.append("时间段：").append(start).append(" ~ ").append(end).append("\n\n");

        grouped.forEach((categoryName, catRecords) -> {
            sb.append("【").append(categoryName).append("】\n");
            catRecords.forEach(r -> {
                String statusLabel = "DONE".equals(r.getStatus()) ? "已完成" : "待办";
                sb.append("- ").append(r.getTitle());
                if (r.getDescription() != null && !r.getDescription().isBlank()) {
                    sb.append("：").append(r.getDescription());
                }
                sb.append("（").append(statusLabel).append("，").append(r.getRecordDate());
                if (!r.getRecordDate().equals(r.getEndDate())) {
                    sb.append("~").append(r.getEndDate());
                }
                sb.append("）\n");
            });
            sb.append("\n");
        });

        return sb.toString();
    }

    private WorkCategoryDTO toCategoryDTO(WorkCategory c) {
        return WorkCategoryDTO.builder()
                .id(c.getId()).name(c.getName()).color(c.getColor())
                .sortOrder(c.getSortOrder()).isDefault(c.isDefault())
                .build();
    }

    private WorkSummaryDTO toSummaryDTO(WorkSummary s) {
        return WorkSummaryDTO.builder()
                .id(s.getId()).startDate(s.getStartDate()).endDate(s.getEndDate())
                .content(s.getContent()).createdAt(s.getCreatedAt())
                .build();
    }

    private WorkRecordDTO toRecordDTO(WorkRecord r, WorkCategory c) {
        String expireStatus = "NORMAL";
        if ("TODO".equals(r.getStatus()) && r.getEndDate() != null) {
            LocalDate today = LocalDate.now();
            LocalDate endDate = r.getEndDate();
            if (endDate.isBefore(today)) {
                expireStatus = "EXPIRED";
            } else if (endDate.isEqual(today.plusDays(1))) {
                expireStatus = "WARN";
            }
        }
        return WorkRecordDTO.builder()
                .id(r.getId()).categoryId(r.getCategoryId())
                .categoryName(c != null ? c.getName() : "")
                .categoryColor(c != null ? c.getColor() : "")
                .title(r.getTitle()).description(r.getDescription())
                .recordDate(r.getRecordDate()).endDate(r.getEndDate())
                .dateType(r.getDateType()).status(r.getStatus())
                .expireStatus(expireStatus)
                .createdAt(r.getCreatedAt()).updatedAt(r.getUpdatedAt())
                .build();
    }
}
