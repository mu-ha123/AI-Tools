package com.ai.toolbox.infrastructure.persistence;

import com.ai.toolbox.domain.overtime.repository.OvertimeSettingsRepository;
import com.ai.toolbox.domain.overtime.valueobject.OvertimeCalculationMode;
import com.ai.toolbox.domain.overtime.valueobject.OvertimePolicy;
import com.ai.toolbox.domain.overtime.valueobject.WorkSchedule;
import com.ai.toolbox.infrastructure.persistence.entity.OvertimeSettingsDO;
import com.ai.toolbox.infrastructure.persistence.repository.JpaOvertimeSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OvertimeSettingsRepositoryImpl implements OvertimeSettingsRepository {

    private final JpaOvertimeSettingsRepository jpaRepository;

    @Override
    public Optional<Settings> find() {
        List<OvertimeSettingsDO> all = jpaRepository.findAll();
        if (all.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toDomain(all.get(0)));
    }

    @Override
    public Settings save(Settings settings) {
        OvertimeSettingsDO entity = jpaRepository.findAll().stream().findFirst().orElse(new OvertimeSettingsDO());
        entity.setWorkStartTime(settings.workSchedule().getWorkStartTime());
        entity.setWorkEndTime(settings.workSchedule().getWorkEndTime());
        entity.setLunchStartTime(settings.workSchedule().getLunchStartTime());
        entity.setLunchEndTime(settings.workSchedule().getLunchEndTime());
        entity.setStandardWorkMinutes(settings.overtimePolicy().getStandardWorkMinutes());
        entity.setCalculationMode(settings.overtimePolicy().getCalculationMode());
        return toDomain(jpaRepository.save(entity));
    }

    private Settings toDomain(OvertimeSettingsDO entity) {
        WorkSchedule schedule = WorkSchedule.of(
                entity.getWorkStartTime(),
                entity.getWorkEndTime(),
                entity.getLunchStartTime(),
                entity.getLunchEndTime());
        OvertimePolicy policy = OvertimePolicy.of(
                entity.getStandardWorkMinutes(),
                entity.getCalculationMode());
        return new Settings(schedule, policy);
    }
}
