package com.ai.toolbox.infrastructure.persistence.repository;

import com.ai.toolbox.infrastructure.persistence.entity.GlossarySystemDO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaGlossarySystemRepository extends JpaRepository<GlossarySystemDO, Long> {

    List<GlossarySystemDO> findAllByOrderBySortOrderAsc();
}
