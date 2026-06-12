package com.ai.toolbox.domain.worklog.repository;

import com.ai.toolbox.domain.worklog.entity.WorkCategory;
import com.ai.toolbox.domain.worklog.entity.WorkRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorklogRepository {

    List<WorkCategory> findAllCategories();

    Optional<WorkCategory> findCategoryById(Long id);

    Optional<WorkCategory> findCategoryByName(String name);

    WorkCategory saveCategory(WorkCategory category);

    void deleteCategory(Long id);

    List<WorkRecord> findRecordsByDateRange(LocalDate start, LocalDate end);

    Optional<WorkRecord> findRecordById(Long id);

    WorkRecord saveRecord(WorkRecord record);

    void deleteRecord(Long id);
}
