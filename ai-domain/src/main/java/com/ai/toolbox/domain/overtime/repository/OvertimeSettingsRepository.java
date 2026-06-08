package com.ai.toolbox.domain.overtime.repository;

import com.ai.toolbox.domain.overtime.valueobject.OvertimePolicy;
import com.ai.toolbox.domain.overtime.valueobject.WorkSchedule;

import java.util.Optional;

public interface OvertimeSettingsRepository {

    Optional<Settings> find();

    Settings save(Settings settings);

    record Settings(WorkSchedule workSchedule, OvertimePolicy overtimePolicy) {
    }
}
