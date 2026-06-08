package com.ai.toolbox.infrastructure.persistence.repository;

import com.ai.toolbox.infrastructure.persistence.entity.GlossaryTermDO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import java.util.Optional;

public interface JpaGlossaryTermRepository extends JpaRepository<GlossaryTermDO, Long> {

    List<GlossaryTermDO> findBySystemIdOrderByNameAsc(Long systemId);

    List<GlossaryTermDO> findBySystemIdAndNameContainingIgnoreCaseOrderByNameAsc(Long systemId, String keyword);

    Optional<GlossaryTermDO> findBySystemIdAndName(Long systemId, String name);
}
