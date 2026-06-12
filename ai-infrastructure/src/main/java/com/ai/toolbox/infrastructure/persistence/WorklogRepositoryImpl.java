package com.ai.toolbox.infrastructure.persistence;

import com.ai.toolbox.domain.worklog.entity.WorkCategory;
import com.ai.toolbox.domain.worklog.entity.WorkRecord;
import com.ai.toolbox.domain.worklog.repository.WorklogRepository;
import com.ai.toolbox.infrastructure.persistence.entity.WorkCategoryDO;
import com.ai.toolbox.infrastructure.persistence.entity.WorkRecordDO;
import com.ai.toolbox.infrastructure.persistence.repository.JpaWorkCategoryRepository;
import com.ai.toolbox.infrastructure.persistence.repository.JpaWorkRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class WorklogRepositoryImpl implements WorklogRepository {

    private final JpaWorkCategoryRepository categoryRepo;
    private final JpaWorkRecordRepository recordRepo;

    @Override
    public List<WorkCategory> findAllCategories() {
        return categoryRepo.findAllByOrderBySortOrderAsc().stream()
                .map(this::toCategoryDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<WorkCategory> findCategoryById(Long id) {
        return categoryRepo.findById(id).map(this::toCategoryDomain);
    }

    @Override
    public Optional<WorkCategory> findCategoryByName(String name) {
        return categoryRepo.findByName(name).map(this::toCategoryDomain);
    }

    @Override
    public WorkCategory saveCategory(WorkCategory category) {
        WorkCategoryDO entity = category.getId() == null
                ? new WorkCategoryDO()
                : categoryRepo.findById(category.getId()).orElse(new WorkCategoryDO());
        entity.setName(category.getName());
        entity.setColor(category.getColor());
        entity.setSortOrder(category.getSortOrder());
        entity.setDefault(category.isDefault());
        if (category.getId() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        WorkCategoryDO saved = categoryRepo.save(entity);
        return toCategoryDomain(saved);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepo.deleteById(id);
    }

    @Override
    public List<WorkRecord> findRecordsByDateRange(LocalDate start, LocalDate end) {
        return recordRepo.findByDateRange(start, end).stream()
                .map(this::toRecordDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<WorkRecord> findRecordById(Long id) {
        return recordRepo.findById(id).map(this::toRecordDomain);
    }

    @Override
    public WorkRecord saveRecord(WorkRecord record) {
        WorkRecordDO entity = record.getId() == null
                ? new WorkRecordDO()
                : recordRepo.findById(record.getId()).orElse(new WorkRecordDO());
        entity.setCategoryId(record.getCategoryId());
        entity.setTitle(record.getTitle());
        entity.setDescription(record.getDescription());
        entity.setRecordDate(record.getRecordDate());
        entity.setEndDate(record.getEndDate());
        entity.setDateType(record.getDateType());
        if (record.getId() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        WorkRecordDO saved = recordRepo.save(entity);
        return toRecordDomain(saved);
    }

    @Override
    public void deleteRecord(Long id) {
        recordRepo.deleteById(id);
    }

    private WorkCategory toCategoryDomain(WorkCategoryDO entity) {
        return WorkCategory.restore(entity.getId(), entity.getName(), entity.getColor(),
                entity.getSortOrder(), entity.isDefault());
    }

    private WorkRecord toRecordDomain(WorkRecordDO entity) {
        return WorkRecord.restore(entity.getId(), entity.getCategoryId(), entity.getTitle(),
                entity.getDescription(), entity.getRecordDate(), entity.getEndDate(), entity.getDateType(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
