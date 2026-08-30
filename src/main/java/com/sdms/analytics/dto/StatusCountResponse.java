package com.sdms.analytics.dto;

import com.sdms.application.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusCountResponse {

    private ApplicationStatus status;
    private Long count;
}