package com.ai.toolbox.application.overtime;

import com.ai.toolbox.application.overtime.dto.AttendanceRecordDTO;
import com.ai.toolbox.application.overtime.dto.MonthlySummaryDTO;
import com.ai.toolbox.application.overtime.dto.OvertimeSettingsDTO;
import com.ai.toolbox.common.util.TimeFormatUtils;
import com.ai.toolbox.domain.overtime.entity.DailyAttendance;
import com.ai.toolbox.domain.overtime.entity.MonthlyOvertimeSummary;
import com.ai.toolbox.domain.overtime.repository.AttendanceRecordRepository;
import com.ai.toolbox.domain.overtime.repository.OvertimeSettingsRepository;
import com.ai.toolbox.domain.overtime.service.OvertimeDomainService;
import com.ai.toolbox.domain.overtime.valueobject.DailyOvertimeResult;
import com.ai.toolbox.domain.overtime.valueobject.OvertimePolicy;
import com.ai.toolbox.domain.overtime.valueobject.WorkSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OvertimeAppService {

    private final OvertimeSettingsRepository settingsRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final OvertimeDomainService overtimeDomainService;

    @Transactional
    public OvertimeSettingsDTO saveSettings(LocalTime workStartTime, LocalTime workEndTime,
                                            LocalTime lunchStartTime, LocalTime lunchEndTime,
                                            int standardWorkMinutes,
                                            com.ai.toolbox.domain.overtime.valueobject.OvertimeCalculationMode calculationMode) {
        WorkSchedule schedule = WorkSchedule.of(workStartTime, workEndTime, lunchStartTime, lunchEndTime);
        OvertimePolicy policy = OvertimePolicy.of(standardWorkMinutes, calculationMode);
        OvertimeSettingsRepository.Settings saved = settingsRepository.save(new OvertimeSettingsRepository.Settings(schedule, policy));
        return toSettingsDTO(saved);
    }

    public OvertimeSettingsDTO getSettings() {
        return settingsRepository.find()
                .map(this::toSettingsDTO)
                .orElseGet(() -> toSettingsDTO(new OvertimeSettingsRepository.Settings(
                        WorkSchedule.of(LocalTime.of(9, 0), LocalTime.of(18, 0), LocalTime.of(12, 0), LocalTime.of(13, 0)),
                        OvertimePolicy.defaultPolicy())));
    }

    @Transactional
    public AttendanceRecordDTO saveRecord(LocalDate workDate, LocalTime clockIn, LocalTime clockOut) {
        OvertimeSettingsRepository.Settings settings = loadSettings();
        DailyAttendance attendance = DailyAttendance.create(workDate, clockIn, clockOut);
        DailyAttendance saved = attendanceRecordRepository.save(attendance);
        DailyOvertimeResult result = overtimeDomainService.calculateDaily(saved, settings.workSchedule(), settings.overtimePolicy());
        return toRecordDTO(saved, result);
    }

    @Transactional
    public List<AttendanceRecordDTO> importRecords(List<LocalDate> workDates, List<LocalTime> clockIns, List<LocalTime> clockOuts) {
        OvertimeSettingsRepository.Settings settings = loadSettings();
        List<AttendanceRecordDTO> results = new java.util.ArrayList<>();
        for (int i = 0; i < workDates.size(); i++) {
            LocalDate workDate = workDates.get(i);
            LocalTime clockIn = clockIns.get(i);
            LocalTime clockOut = clockOuts.get(i);
            DailyAttendance attendance = DailyAttendance.create(workDate, clockIn, clockOut);
            DailyAttendance saved = attendanceRecordRepository.save(attendance);
            DailyOvertimeResult result = overtimeDomainService.calculateDaily(saved, settings.workSchedule(), settings.overtimePolicy());
            results.add(toRecordDTO(saved, result));
        }
        return results;
    }

    public MonthlySummaryDTO getMonthlySummary(int year, int month) {
        OvertimeSettingsRepository.Settings settings = loadSettings();
        YearMonth yearMonth = YearMonth.of(year, month);
        List<DailyAttendance> records = attendanceRecordRepository.findByYearMonth(yearMonth);
        MonthlyOvertimeSummary summary = overtimeDomainService.buildMonthlySummary(yearMonth, records);
        List<DailyOvertimeResult> breakdown = summary.dailyBreakdown(settings.workSchedule(), settings.overtimePolicy());
        long totalMinutes = summary.totalMinutes(settings.workSchedule(), settings.overtimePolicy());

        List<AttendanceRecordDTO> dailyRecords = breakdown.stream()
                .map(result -> {
                    DailyAttendance attendance = records.stream()
                            .filter(item -> item.getWorkDate().equals(result.getWorkDate()))
                            .findFirst()
                            .orElseThrow();
                    return toRecordDTO(attendance, result);
                })
                .collect(Collectors.toList());

        return MonthlySummaryDTO.builder()
                .year(year)
                .month(month)
                .totalOvertimeMinutes(totalMinutes)
                .totalOvertimeText(TimeFormatUtils.formatMinutes(totalMinutes))
                .dailyRecords(dailyRecords)
                .build();
    }

    private OvertimeSettingsRepository.Settings loadSettings() {
        return settingsRepository.find().orElseGet(() -> new OvertimeSettingsRepository.Settings(
                WorkSchedule.of(LocalTime.of(9, 0), LocalTime.of(18, 0), LocalTime.of(12, 0), LocalTime.of(13, 0)),
                OvertimePolicy.defaultPolicy()));
    }

    private OvertimeSettingsDTO toSettingsDTO(OvertimeSettingsRepository.Settings settings) {
        WorkSchedule schedule = settings.workSchedule();
        OvertimePolicy policy = settings.overtimePolicy();
        return OvertimeSettingsDTO.builder()
                .workStartTime(schedule.getWorkStartTime())
                .workEndTime(schedule.getWorkEndTime())
                .lunchStartTime(schedule.getLunchStartTime())
                .lunchEndTime(schedule.getLunchEndTime())
                .standardWorkMinutes(policy.getStandardWorkMinutes())
                .calculationMode(policy.getCalculationMode())
                .build();
    }

    @Transactional
    public AttendanceRecordDTO toggleLeave(LocalDate workDate) {
        OvertimeSettingsRepository.Settings settings = loadSettings();
        DailyAttendance attendance = attendanceRecordRepository.findByDate(workDate)
                .map(existing -> {
                    if (existing.isLeave()) {
                        attendanceRecordRepository.deleteByDate(workDate);
                        return null;
                    }
                    return existing;
                })
                .orElseGet(() -> DailyAttendance.createLeave(workDate));
        if (attendance == null) {
            return null;
        }
        DailyAttendance saved = attendanceRecordRepository.save(attendance);
        DailyOvertimeResult result = overtimeDomainService.calculateDaily(saved, settings.workSchedule(), settings.overtimePolicy());
        return toRecordDTO(saved, result);
    }

    private AttendanceRecordDTO toRecordDTO(DailyAttendance attendance, DailyOvertimeResult result) {
        return AttendanceRecordDTO.builder()
                .id(attendance.getId())
                .workDate(attendance.getWorkDate())
                .clockIn(attendance.getClockIn())
                .clockOut(attendance.getClockOut())
                .overtimeMinutes(result.getOvertimeMinutes())
                .earlyOvertimeMinutes(result.getEarlyOvertimeMinutes())
                .lateOvertimeMinutes(result.getLateOvertimeMinutes())
                .actualWorkMinutes(result.getActualWorkMinutes())
                .isLeave(attendance.isLeave())
                .build();
    }
}
