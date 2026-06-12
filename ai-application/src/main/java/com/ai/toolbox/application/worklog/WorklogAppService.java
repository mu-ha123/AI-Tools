package com.ai.toolbox.application.worklog;

import com.ai.toolbox.application.worklog.dto.WorkCategoryDTO;
import com.ai.toolbox.application.worklog.dto.WorkRecordDTO;
import com.ai.toolbox.common.exception.BizException;
import com.ai.toolbox.common.result.ErrorCode;
import com.ai.toolbox.domain.worklog.entity.WorkCategory;
import com.ai.toolbox.domain.worklog.entity.WorkRecord;
import com.ai.toolbox.domain.worklog.repository.WorklogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
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

    public List<WorkRecordDTO> listRecords(String viewType, LocalDate date, Long categoryId) {
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
        return queryRecordsInRange(start, end, categoryId);
    }

    public List<WorkRecordDTO> listRecordsByRange(LocalDate start, LocalDate end, Long categoryId) {
        if (start == null) start = LocalDate.now().withDayOfMonth(1);
        if (end == null) end = LocalDate.now();
        return queryRecordsInRange(start, end, categoryId);
    }

    private List<WorkRecordDTO> queryRecordsInRange(LocalDate start, LocalDate end, Long categoryId) {
        Map<Long, WorkCategory> categoryMap = worklogRepository.findAllCategories().stream()
                .collect(Collectors.toMap(WorkCategory::getId, c -> c));

        return worklogRepository.findRecordsByDateRange(start, end).stream()
                .filter(r -> categoryId == null || r.getCategoryId().equals(categoryId))
                .map(r -> toRecordDTO(r, categoryMap.get(r.getCategoryId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkRecordDTO createRecord(Long categoryId, String title, String description,
                                      LocalDate recordDate, LocalDate endDate, String dateType) {
        if (worklogRepository.findCategoryById(categoryId).isEmpty()) {
            throw new BizException(ErrorCode.WORK_CATEGORY_NOT_FOUND);
        }
        if (endDate == null) {
            endDate = recordDate;
        }
        WorkRecord saved = worklogRepository.saveRecord(
                WorkRecord.create(categoryId, title, description, recordDate, endDate, dateType != null ? dateType : "DAY"));
        return toRecordDTO(saved, null);
    }

    @Transactional
    public WorkRecordDTO updateRecord(Long id, Long categoryId, String title, String description,
                                       LocalDate recordDate, LocalDate endDate, String dateType) {
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

        WorkRecord updated = WorkRecord.restore(id, finalCategoryId, finalTitle, finalDesc,
                finalStart, finalEnd, finalDateType, existing.getCreatedAt(), null);
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
        return queryRecordsInRange(start, end, null);
    }

    private WorkCategoryDTO toCategoryDTO(WorkCategory c) {
        return WorkCategoryDTO.builder()
                .id(c.getId()).name(c.getName()).color(c.getColor())
                .sortOrder(c.getSortOrder()).isDefault(c.isDefault())
                .build();
    }

    private WorkRecordDTO toRecordDTO(WorkRecord r, WorkCategory c) {
        return WorkRecordDTO.builder()
                .id(r.getId()).categoryId(r.getCategoryId())
                .categoryName(c != null ? c.getName() : "")
                .categoryColor(c != null ? c.getColor() : "")
                .title(r.getTitle()).description(r.getDescription())
                .recordDate(r.getRecordDate()).endDate(r.getEndDate())
                .dateType(r.getDateType())
                .createdAt(r.getCreatedAt()).updatedAt(r.getUpdatedAt())
                .build();
    }
}
