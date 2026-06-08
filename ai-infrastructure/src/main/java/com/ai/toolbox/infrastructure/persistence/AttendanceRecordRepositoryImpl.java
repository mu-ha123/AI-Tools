package com.ai.toolbox.infrastructure.persistence;

import com.ai.toolbox.domain.overtime.entity.DailyAttendance;
import com.ai.toolbox.domain.overtime.repository.AttendanceRecordRepository;
import com.ai.toolbox.infrastructure.persistence.entity.AttendanceRecordDO;
import com.ai.toolbox.infrastructure.persistence.repository.JpaAttendanceRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AttendanceRecordRepositoryImpl implements AttendanceRecordRepository {

    private final JpaAttendanceRecordRepository jpaRepository;

    @Override
    public DailyAttendance save(DailyAttendance attendance) {
        AttendanceRecordDO entity = jpaRepository.findByWorkDate(attendance.getWorkDate())
                .orElse(new AttendanceRecordDO());
        entity.setWorkDate(attendance.getWorkDate());
        entity.setClockIn(attendance.getClockIn());
        entity.setClockOut(attendance.getClockOut());
        entity.setIsLeave(attendance.isLeave());
        AttendanceRecordDO saved = jpaRepository.save(entity);
        return DailyAttendance.restore(saved.getId(), saved.getWorkDate(), saved.getClockIn(), saved.getClockOut(), saved.getIsLeave());
    }

    @Override
    public Optional<DailyAttendance> findByDate(LocalDate workDate) {
        return jpaRepository.findByWorkDate(workDate)
                .map(this::toDomain);
    }

    @Override
    public List<DailyAttendance> findByYearMonth(YearMonth yearMonth) {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        return jpaRepository.findByWorkDateBetween(start, end).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByDate(LocalDate workDate) {
        jpaRepository.findByWorkDate(workDate).ifPresent(jpaRepository::delete);
    }

    private DailyAttendance toDomain(AttendanceRecordDO entity) {
        return DailyAttendance.restore(entity.getId(), entity.getWorkDate(), entity.getClockIn(), entity.getClockOut(), entity.getIsLeave());
    }
}
