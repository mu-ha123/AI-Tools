package com.ai.toolbox.interfaces.controller;

import com.ai.toolbox.application.tool.ToolRegistryAppService;
import com.ai.toolbox.common.result.Result;
import com.ai.toolbox.common.tool.ToolDescriptor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tools")
@RequiredArgsConstructor
public class ToolController {

    private final ToolRegistryAppService toolRegistryAppService;

    @GetMapping
    public Result<List<ToolDescriptor>> listTools() {
        return Result.success(toolRegistryAppService.listTools());
    }
}
