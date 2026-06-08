package com.ai.toolbox.domain.glossary.repository;

import com.ai.toolbox.domain.glossary.entity.GlossaryChangeHistory;
import com.ai.toolbox.domain.glossary.entity.GlossarySystem;
import com.ai.toolbox.domain.glossary.entity.GlossaryTerm;

import java.util.List;
import java.util.Optional;

public interface GlossaryRepository {

    List<GlossarySystem> findAllSystems();

    GlossarySystem saveSystem(GlossarySystem system);

    void deleteSystem(Long id);

    List<GlossaryTerm> findTermsBySystemId(Long systemId);

    List<GlossaryTerm> findTermsByNameLike(Long systemId, String keyword);

    Optional<GlossaryTerm> findTermById(Long id);

    Optional<GlossaryTerm> findTermBySystemIdAndName(Long systemId, String name);

    GlossaryTerm saveTerm(GlossaryTerm term);

    void deleteTerm(Long id);

    List<GlossaryChangeHistory> findHistoryByTermId(Long termId);

    GlossaryChangeHistory saveHistory(GlossaryChangeHistory history);
}
