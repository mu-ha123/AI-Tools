package com.ai.toolbox.infrastructure.persistence.repository;

import com.ai.toolbox.infrastructure.persistence.entity.WorkCategoryDO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaWorkCategoryRepository extends JpaRepository<WorkCategoryDO, Long> {

    List<WorkCategoryDO> findAllByOrderBySortOrderAsc();

    Optional<WorkCategoryDO> findByName(String name);
}
