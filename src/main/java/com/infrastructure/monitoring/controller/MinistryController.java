package com.infrastructure.monitoring.controller;

import com.infrastructure.monitoring.entity.Ministry;
import com.infrastructure.monitoring.service.MinistryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ministries")
public class MinistryController {

    private final MinistryService ministryService;

    public MinistryController(MinistryService ministryService) {
        this.ministryService = ministryService;
    }

    @PostMapping
    public Ministry createMinistry(@RequestBody Ministry ministry) {
        return ministryService.createMinistry(ministry);
    }

    @GetMapping
    public List<Ministry> getAllMinistries() {
        return ministryService.getAllMinistries();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ministry> getMinistryById(@PathVariable Long id) {
        return ministryService.getMinistryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}