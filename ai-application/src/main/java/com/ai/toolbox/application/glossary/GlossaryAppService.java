package com.ai.toolbox.application.glossary;

import com.ai.toolbox.application.glossary.dto.GlossaryHistoryDTO;
import com.ai.toolbox.application.glossary.dto.GlossarySystemDTO;
import com.ai.toolbox.application.glossary.dto.GlossaryTermDTO;
import com.ai.toolbox.common.exception.BizException;
import com.ai.toolbox.common.result.ErrorCode;
import com.ai.toolbox.domain.glossary.entity.GlossaryChangeHistory;
import com.ai.toolbox.domain.glossary.entity.GlossarySystem;
import com.ai.toolbox.domain.glossary.entity.GlossaryTerm;
import com.ai.toolbox.domain.glossary.repository.GlossaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GlossaryAppService {

    private final GlossaryRepository glossaryRepository;

    public List<GlossarySystemDTO> listSystems() {
        return glossaryRepository.findAllSystems().stream()
                .map(this::toSystemDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public GlossarySystemDTO createSystem(String name) {
        int maxOrder = glossaryRepository.findAllSystems().size();
        GlossarySystem saved = glossaryRepository.saveSystem(GlossarySystem.create(name, maxOrder + 1));
        return toSystemDTO(saved);
    }

    @Transactional
    public void deleteSystem(Long id) {
        glossaryRepository.deleteSystem(id);
    }

    public List<GlossaryTermDTO> listTerms(Long systemId, String keyword) {
        List<GlossaryTerm> terms;
        if (keyword == null || keyword.isBlank()) {
            terms = glossaryRepository.findTermsBySystemId(systemId);
        } else {
            terms = glossaryRepository.findTermsByNameLike(systemId, keyword);
        }
        return terms.stream().map(this::toTermDTO).collect(Collectors.toList());
    }

    public GlossaryTermDTO getTerm(Long id) {
        return glossaryRepository.findTermById(id)
                .map(this::toTermDTO)
                .orElse(null);
    }

    @Transactional
    public GlossaryTermDTO createTerm(Long systemId, String name, String description, String category) {
        if (glossaryRepository.findTermBySystemIdAndName(systemId, name).isPresent()) {
            throw new BizException(ErrorCode.GLOSSARY_DUPLICATE, "名词 '" + name + "' 已存在");
        }
        GlossaryTerm saved = glossaryRepository.saveTerm(GlossaryTerm.create(systemId, name, description, category));
        return toTermDTO(saved);
    }

    @Transactional
    public GlossaryTermDTO updateTerm(Long id, String name, String description, String category) {
        GlossaryTerm existing = glossaryRepository.findTermById(id)
                .orElseThrow(() -> new RuntimeException("名词不存在"));
        if (!existing.getName().equals(name)) {
            glossaryRepository.saveHistory(GlossaryChangeHistory.create(id, "name", existing.getName(), name));
        }
        if (!strEquals(existing.getDescription(), description)) {
            glossaryRepository.saveHistory(GlossaryChangeHistory.create(id, "description", existing.getDescription(), description));
        }
        if (!strEquals(existing.getCategory(), category)) {
            glossaryRepository.saveHistory(GlossaryChangeHistory.create(id, "category", existing.getCategory(), category));
        }
        GlossaryTerm updated = GlossaryTerm.restore(id, existing.getSystemId(), name, description, category,
                existing.getCreatedAt(), null);
        GlossaryTerm saved = glossaryRepository.saveTerm(updated);
        return toTermDTO(saved);
    }

    @Transactional
    public ImportResult importTerms(Long systemId, List<ImportRow> rows) {
        List<GlossaryTermDTO> results = new ArrayList<>();
        List<String> duplicates = new ArrayList<>();
        for (ImportRow row : rows) {
            Optional<GlossaryTerm> existing = glossaryRepository.findTermBySystemIdAndName(systemId, row.name());
            if (existing.isPresent()) {
                duplicates.add(row.name());
                continue;
            }
            GlossaryTerm saved = glossaryRepository.saveTerm(
                    GlossaryTerm.create(systemId, row.name(), row.description(), null));
            results.add(toTermDTO(saved));
        }
        return new ImportResult(results, duplicates);
    }

    public record ImportRow(String name, String description) {}

    public record ImportResult(List<GlossaryTermDTO> imported, List<String> duplicates) {}

    @Transactional
    public void deleteTerm(Long id) {
        glossaryRepository.deleteTerm(id);
    }

    public List<GlossaryHistoryDTO> listHistory(Long termId) {
        return glossaryRepository.findHistoryByTermId(termId).stream()
                .map(this::toHistoryDTO)
                .collect(Collectors.toList());
    }

    private boolean strEquals(String a, String b) {
        return (a == null ? "" : a).equals(b == null ? "" : b);
    }

    private GlossarySystemDTO toSystemDTO(GlossarySystem s) {
        return GlossarySystemDTO.builder()
                .id(s.getId()).name(s.getName()).sortOrder(s.getSortOrder()).build();
    }

    private GlossaryTermDTO toTermDTO(GlossaryTerm t) {
        return GlossaryTermDTO.builder()
                .id(t.getId()).systemId(t.getSystemId()).name(t.getName())
                .description(t.getDescription()).category(t.getCategory())
                .createdAt(t.getCreatedAt()).updatedAt(t.getUpdatedAt())
                .build();
    }

    private GlossaryHistoryDTO toHistoryDTO(GlossaryChangeHistory h) {
        return GlossaryHistoryDTO.builder()
                .id(h.getId()).termId(h.getTermId()).fieldName(h.getFieldName())
                .oldValue(h.getOldValue()).newValue(h.getNewValue()).changedAt(h.getChangedAt())
                .build();
    }
}
