package com.ai.toolbox.interfaces.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiAnalyzeRequest {

    @Min(2000)
    @Max(2100)
    private int year;

    @Min(1)
    @Max(12)
    private int month;
}
