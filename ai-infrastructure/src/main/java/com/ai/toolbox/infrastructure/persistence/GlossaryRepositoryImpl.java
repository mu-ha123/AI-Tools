package com.ai.toolbox.infrastructure.persistence;

import com.ai.toolbox.domain.glossary.entity.GlossaryChangeHistory;
import com.ai.toolbox.domain.glossary.entity.GlossarySystem;
import com.ai.toolbox.domain.glossary.entity.GlossaryTerm;
import com.ai.toolbox.domain.glossary.repository.GlossaryRepository;
import com.ai.toolbox.infrastructure.persistence.entity.GlossaryChangeHistoryDO;
import com.ai.toolbox.infrastructure.persistence.entity.GlossarySystemDO;
import com.ai.toolbox.infrastructure.persistence.entity.GlossaryTermDO;
import com.ai.toolbox.infrastructure.persistence.repository.JpaGlossaryChangeHistoryRepository;
import com.ai.toolbox.infrastructure.persistence.repository.JpaGlossarySystemRepository;
import com.ai.toolbox.infrastructure.persistence.repository.JpaGlossaryTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GlossaryRepositoryImpl implements GlossaryRepository {

    private final JpaGlossarySystemRepository systemRepo;
    private final JpaGlossaryTermRepository termRepo;
    private final JpaGlossaryChangeHistoryRepository historyRepo;

    @Override
    public List<GlossarySystem> findAllSystems() {
        return systemRepo.findAllByOrderBySortOrderAsc().stream()
                .map(this::toSystemDomain)
                .collect(Collectors.toList());
    }

    @Override
    public GlossarySystem saveSystem(GlossarySystem system) {
        GlossarySystemDO entity = system.getId() == null
                ? new GlossarySystemDO()
                : systemRepo.findById(system.getId()).orElse(new GlossarySystemDO());
        entity.setName(system.getName());
        entity.setSortOrder(system.getSortOrder());
        GlossarySystemDO saved = systemRepo.save(entity);
        return toSystemDomain(saved);
    }

    @Override
    public void deleteSystem(Long id) {
        systemRepo.deleteById(id);
    }

    @Override
    public List<GlossaryTerm> findTermsBySystemId(Long systemId) {
        return termRepo.findBySystemIdOrderByNameAsc(systemId).stream()
                .map(this::toTermDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<GlossaryTerm> findTermsByNameLike(Long systemId, String keyword) {
        return termRepo.findBySystemIdAndNameContainingIgnoreCaseOrderByNameAsc(systemId, keyword).stream()
                .map(this::toTermDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<GlossaryTerm> findTermById(Long id) {
        return termRepo.findById(id).map(this::toTermDomain);
    }

    @Override
    public Optional<GlossaryTerm> findTermBySystemIdAndName(Long systemId, String name) {
        return termRepo.findBySystemIdAndName(systemId, name).map(this::toTermDomain);
    }

    @Override
    public GlossaryTerm saveTerm(GlossaryTerm term) {
        GlossaryTermDO entity = term.getId() == null
                ? new GlossaryTermDO()
                : termRepo.findById(term.getId()).orElse(new GlossaryTermDO());
        entity.setSystemId(term.getSystemId());
        entity.setName(term.getName());
        entity.setDescription(term.getDescription());
        entity.setCategory(term.getCategory());
        if (term.getId() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        GlossaryTermDO saved = termRepo.save(entity);
        return toTermDomain(saved);
    }

    @Override
    public void deleteTerm(Long id) {
        termRepo.deleteById(id);
    }

    @Override
    public List<GlossaryChangeHistory> findHistoryByTermId(Long termId) {
        return historyRepo.findByTermIdOrderByChangedAtDesc(termId).stream()
                .map(this::toHistoryDomain)
                .collect(Collectors.toList());
    }

    @Override
    public GlossaryChangeHistory saveHistory(GlossaryChangeHistory history) {
        GlossaryChangeHistoryDO entity = new GlossaryChangeHistoryDO();
        entity.setTermId(history.getTermId());
        entity.setFieldName(history.getFieldName());
        entity.setOldValue(history.getOldValue());
        entity.setNewValue(history.getNewValue());
        entity.setChangedAt(LocalDateTime.now());
        GlossaryChangeHistoryDO saved = historyRepo.save(entity);
        return toHistoryDomain(saved);
    }

    private GlossarySystem toSystemDomain(GlossarySystemDO entity) {
        return GlossarySystem.restore(entity.getId(), entity.getName(), entity.getSortOrder());
    }

    private GlossaryTerm toTermDomain(GlossaryTermDO entity) {
        return GlossaryTerm.restore(entity.getId(), entity.getSystemId(), entity.getName(),
                entity.getDescription(), entity.getCategory(), entity.getCreatedAt(), entity.getUpdatedAt());
    }

    private GlossaryChangeHistory toHistoryDomain(GlossaryChangeHistoryDO entity) {
        return GlossaryChangeHistory.restore(entity.getId(), entity.getTermId(), entity.getFieldName(),
                entity.getOldValue(), entity.getNewValue(), entity.getChangedAt());
    }
}
