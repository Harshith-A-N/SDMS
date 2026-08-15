package com.sdms.scheme.controller;

import com.sdms.scheme.dto.SchemeCreateRequest;
import com.sdms.scheme.dto.SchemeResponse;
import com.sdms.scheme.service.SchemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor  // this is for dependency injection
@RequestMapping("/api/schemes")  // for the base  url
public class SchemeController {

    private final SchemeService schemeService;

    @PostMapping
    public ResponseEntity<SchemeResponse> createScheme(@RequestBody SchemeCreateRequest request){
        SchemeResponse created = schemeService.createScheme(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SchemeResponse>> getAllActiveSchemes(){
        return ResponseEntity.ok(schemeService.getAllActiveSchemes()); // this is equivalent to writing return new ResponseEntity<>(schemeService.getAllActiveSchemes(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchemeResponse> getSchemeById(@PathVariable Long id){
        return ResponseEntity.ok(schemeService.getSchemeById(id));
    }

    @GetMapping("/region/{region}")
    public ResponseEntity<List<SchemeResponse>> getSchemesByRegion(@PathVariable String region){
        return ResponseEntity.ok(schemeService.getSchemesByRegion(region));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchemeResponse> updateScheme(@PathVariable Long id, @RequestBody SchemeCreateRequest request){
        return ResponseEntity.ok(schemeService.updateScheme(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateScheme(@PathVariable Long id){
        schemeService.deactivateScheme(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateScheme(@PathVariable Long id){
        schemeService.activateScheme(id);
        return ResponseEntity.noContent().build();
    }
}
