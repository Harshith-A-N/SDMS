package com.sdms.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotesRequest {

    @NotBlank(message = "notes is required")
    private String notes;
}