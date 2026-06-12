package com.ai.toolbox.infrastructure.persistence.repository;

import com.ai.toolbox.infrastructure.persistence.entity.WorkRecordDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface JpaWorkRecordRepository extends JpaRepository<WorkRecordDO, Long> {

    @Query("SELECT r FROM WorkRecordDO r WHERE r.recordDate <= :end AND r.endDate >= :start ORDER BY r.recordDate DESC, r.id DESC")
    List<WorkRecordDO> findByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
