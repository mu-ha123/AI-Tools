package com.ai.toolbox.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "attendance_record", uniqueConstraints = {
        @UniqueConstraint(name = "uk_attendance_work_date", columnNames = "work_date")
})
public class AttendanceRecordDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(nullable = false)
    private LocalTime clockIn;

    @Column(nullable = false)
    private LocalTime clockOut;

    @Column(nullable = false)
    private Boolean isLeave = false;
}
