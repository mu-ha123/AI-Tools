package com.ai.toolbox.application.tool;

import com.ai.toolbox.common.tool.ToolDescriptor;
import com.ai.toolbox.common.tool.ToolType;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ToolRegistryAppService {

    public List<ToolDescriptor> listTools() {
        return Arrays.stream(ToolType.values())
                .map(ToolDescriptor::from)
                .collect(Collectors.toList());
    }
}
