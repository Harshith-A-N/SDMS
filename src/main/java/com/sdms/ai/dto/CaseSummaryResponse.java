package com.sdms.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaseSummaryResponse {

    private Long applicationId;
    private String summary;
}