package com.ai.toolbox.infrastructure.persistence.repository;

import com.ai.toolbox.infrastructure.persistence.entity.AttendanceRecordDO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JpaAttendanceRecordRepository extends JpaRepository<AttendanceRecordDO, Long> {

    Optional<AttendanceRecordDO> findByWorkDate(LocalDate workDate);

    List<AttendanceRecordDO> findByWorkDateBetween(LocalDate start, LocalDate end);
}
