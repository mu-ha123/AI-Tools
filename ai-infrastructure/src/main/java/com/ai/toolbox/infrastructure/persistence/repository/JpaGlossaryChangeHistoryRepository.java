package com.ai.toolbox.infrastructure.persistence.repository;

import com.ai.toolbox.infrastructure.persistence.entity.GlossaryChangeHistoryDO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaGlossaryChangeHistoryRepository extends JpaRepository<GlossaryChangeHistoryDO, Long> {

    List<GlossaryChangeHistoryDO> findByTermIdOrderByChangedAtDesc(Long termId);
}
