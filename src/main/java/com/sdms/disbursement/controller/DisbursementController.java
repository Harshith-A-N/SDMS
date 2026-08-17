package com.sdms.disbursement.controller;

import com.sdms.disbursement.dto.*;
import com.sdms.disbursement.service.DisbursementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disbursements")
@RequiredArgsConstructor
public class DisbursementController {

    private final DisbursementService disbursementService;

    @GetMapping
    public ResponseEntity<List<DisbursementResponse>> getAllDisbursements() {
        return ResponseEntity.ok(disbursementService.getAllDisbursements());
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<DisbursementResponse>> getDisbursementsByApplication(
            @PathVariable Long applicationId) {
        return ResponseEntity.ok(disbursementService.getDisbursementsByApplication(applicationId));
    }

    @PatchMapping("/{id}/release")
    public ResponseEntity<DisbursementResponse> release(
            @PathVariable Long id,
            @RequestBody DisbursementReleaseRequest request) {
        return ResponseEntity.ok(disbursementService.release(id, request));
    }

    @PatchMapping("/{id}/mark-failed")
    public ResponseEntity<DisbursementResponse> markAsFailed(
            @PathVariable Long id,
            @RequestBody DisbursementReleaseRequest request) {
        return ResponseEntity.ok(disbursementService.markAsFailed(id, request));
    }

    @PatchMapping("/{id}/retry")
    public ResponseEntity<DisbursementResponse> retry(@PathVariable Long id) {
        return ResponseEntity.ok(disbursementService.retry(id));
    }
}