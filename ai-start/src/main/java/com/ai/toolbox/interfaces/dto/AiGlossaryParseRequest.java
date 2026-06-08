package com.ai.toolbox.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiGlossaryParseRequest {

    @NotNull
    private Long systemId;

    @NotBlank
    private String text;
}
