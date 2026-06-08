package com.ai.toolbox.infrastructure.persistence.repository;

import com.ai.toolbox.infrastructure.persistence.entity.OvertimeSettingsDO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOvertimeSettingsRepository extends JpaRepository<OvertimeSettingsDO, Long> {
}
