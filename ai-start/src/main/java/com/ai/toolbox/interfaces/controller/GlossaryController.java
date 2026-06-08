package com.ai.toolbox.interfaces.controller;

import com.ai.toolbox.application.glossary.GlossaryAppService;
import com.ai.toolbox.application.glossary.dto.GlossaryHistoryDTO;
import com.ai.toolbox.application.glossary.dto.GlossarySystemDTO;
import com.ai.toolbox.application.glossary.dto.GlossaryTermDTO;
import com.ai.toolbox.common.result.ErrorCode;
import com.ai.toolbox.common.exception.BizException;
import com.ai.toolbox.common.result.Result;
import com.ai.toolbox.infrastructure.ai.GlossaryAiService;
import com.ai.toolbox.interfaces.dto.AiGlossaryParseRequest;
import com.ai.toolbox.interfaces.util.ExcelImportUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/glossary")
@RequiredArgsConstructor
public class GlossaryController {

    private final GlossaryAppService glossaryAppService;
    private final GlossaryAiService glossaryAiService;

    @GetMapping("/systems")
    public Result<java.util.List<GlossarySystemDTO>> listSystems() {
        return Result.success(glossaryAppService.listSystems());
    }

    @PostMapping("/systems")
    public Result<GlossarySystemDTO> createSystem(@RequestBody Map<String, String> body) {
        return Result.success(glossaryAppService.createSystem(body.get("name")));
    }

    @DeleteMapping("/systems/{id}")
    public Result<Void> deleteSystem(@PathVariable Long id) {
        glossaryAppService.deleteSystem(id);
        return Result.success();
    }

    @PostMapping("/terms/import")
    public Result<Map<String, Object>> importTerms(
            @RequestParam("systemId") Long systemId,
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BizException(ErrorCode.OVERTIME_IMPORT_ERROR, "上传文件为空");
        }
        List<ExcelImportUtil.GlossaryExcelRow> rows;
        try {
            rows = ExcelImportUtil.parseGlossary(file.getInputStream());
        } catch (IOException e) {
            throw new BizException(ErrorCode.OVERTIME_IMPORT_ERROR, "文件读取失败");
        }
        if (rows.isEmpty()) {
            throw new BizException(ErrorCode.OVERTIME_IMPORT_ERROR, "未找到有效数据");
        }
        List<GlossaryAppService.ImportRow> importRows = rows.stream()
                .map(r -> new GlossaryAppService.ImportRow(r.name(), r.description()))
                .collect(Collectors.toList());
        GlossaryAppService.ImportResult importResult = glossaryAppService.importTerms(systemId, importRows);
        Map<String, Object> result = new HashMap<>();
        result.put("total", importResult.imported().size());
        result.put("duplicates", importResult.duplicates());
        result.put("terms", importResult.imported());
        return Result.success(result);
    }

    @PostMapping("/ai/parse")
    public Result<GlossaryTermDTO> parseByAi(@Valid @RequestBody AiGlossaryParseRequest request) {
        GlossaryAiService.ParsedTerm parsed = glossaryAiService.parseTerm(request.getText());
        GlossaryTermDTO saved = glossaryAppService.createTerm(
                request.getSystemId(), parsed.name(), parsed.description(), parsed.category());
        return Result.success(saved);
    }

    @GetMapping("/terms")
    public Result<java.util.List<GlossaryTermDTO>> listTerms(
            @RequestParam("systemId") Long systemId,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return Result.success(glossaryAppService.listTerms(systemId, keyword));
    }

    @GetMapping("/terms/{id}")
    public Result<GlossaryTermDTO> getTerm(@PathVariable Long id) {
        GlossaryTermDTO term = glossaryAppService.getTerm(id);
        if (term == null) {
            return Result.fail("A01-GLOSSARY-001", "名词不存在");
        }
        return Result.success(term);
    }

    @PostMapping("/terms")
    public Result<GlossaryTermDTO> createTerm(@RequestBody Map<String, Object> body) {
        Long systemId = Long.valueOf(body.get("systemId").toString());
        String name = (String) body.get("name");
        String description = (String) body.get("description");
        String category = (String) body.get("category");
        return Result.success(glossaryAppService.createTerm(systemId, name, description, category));
    }

    @PutMapping("/terms/{id}")
    public Result<GlossaryTermDTO> updateTerm(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        String description = (String) body.get("description");
        String category = (String) body.get("category");
        return Result.success(glossaryAppService.updateTerm(id, name, description, category));
    }

    @DeleteMapping("/terms/{id}")
    public Result<Void> deleteTerm(@PathVariable Long id) {
        glossaryAppService.deleteTerm(id);
        return Result.success();
    }

    @GetMapping("/terms/{id}/history")
    public Result<java.util.List<GlossaryHistoryDTO>> listHistory(@PathVariable Long id) {
        return Result.success(glossaryAppService.listHistory(id));
    }
}
