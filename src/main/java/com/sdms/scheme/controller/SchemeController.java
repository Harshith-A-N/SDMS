package com.sdms.scheme.controller;

import com.sdms.scheme.dto.SchemeCreateRequest;
import com.sdms.scheme.dto.SchemeResponse;
import com.sdms.scheme.service.SchemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schemes")
public class SchemeController {

    private final SchemeService schemeService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SchemeResponse> createScheme(@RequestBody SchemeCreateRequest request){
        SchemeResponse created = schemeService.createScheme(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN','FIELD_OFFICER','DISTRICT_OFFICER','FINANCE_APPROVER','BENEFICIARY')")
    @GetMapping
    public ResponseEntity<List<SchemeResponse>> getAllActiveSchemes(){
        return ResponseEntity.ok(schemeService.getAllActiveSchemes());
    }

    @PreAuthorize("hasAnyRole('ADMIN','FIELD_OFFICER','DISTRICT_OFFICER','FINANCE_APPROVER','BENEFICIARY')")
    @GetMapping("/{id}")
    public ResponseEntity<SchemeResponse> getSchemeById(@PathVariable Long id){
        return ResponseEntity.ok(schemeService.getSchemeById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','FIELD_OFFICER','DISTRICT_OFFICER','FINANCE_APPROVER','BENEFICIARY')")
    @GetMapping("/region/{region}")
    public ResponseEntity<List<SchemeResponse>> getSchemesByRegion(@PathVariable String region){
        return ResponseEntity.ok(schemeService.getSchemesByRegion(region));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SchemeResponse> updateScheme(@PathVariable Long id, @RequestBody SchemeCreateRequest request){
        return ResponseEntity.ok(schemeService.updateScheme(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateScheme(@PathVariable Long id){
        schemeService.deactivateScheme(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateScheme(@PathVariable Long id){
        schemeService.activateScheme(id);
        return ResponseEntity.noContent().build();
    }
}