package com.infrastructure.monitoring.service;

import com.infrastructure.monitoring.entity.Ministry;
import com.infrastructure.monitoring.repository.MinistryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MinistryService {

    private final MinistryRepository ministryRepository;

    public MinistryService(MinistryRepository ministryRepository) {
        this.ministryRepository = ministryRepository;
    }

    public Ministry createMinistry(Ministry ministry) {
        return ministryRepository.save(ministry);
    }

    public List<Ministry> getAllMinistries() {
        return ministryRepository.findAll();
    }

    public Optional<Ministry> getMinistryById(Long id) {
        return ministryRepository.findById(id);
    }
}