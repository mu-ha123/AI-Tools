package com.ai.toolbox.infrastructure.persistence.repository;

import com.ai.toolbox.infrastructure.persistence.entity.WorkSummaryDO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JpaWorkSummaryRepository extends JpaRepository<WorkSummaryDO, Long> {

    List<WorkSummaryDO> findAllByOrderByCreatedAtDesc();

    Optional<WorkSummaryDO> findByStartDateAndEndDate(LocalDate startDate, LocalDate endDate);
}
