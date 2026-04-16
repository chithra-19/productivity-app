package com.climbup.service.productivity;

import java.util.List;

import org.springframework.stereotype.Service;

import com.climbup.model.AchievementCode;
import com.climbup.model.AchievementTemplate;
import com.climbup.repository.AchievementTemplateRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AchievementTemplateService {

    private final AchievementTemplateRepository repository;

    public AchievementTemplateService(AchievementTemplateRepository repository) {
        this.repository = repository;
    }

    public List<AchievementTemplate> getAll() {
        return repository.findAll();
    }

    public AchievementTemplate getByCode(AchievementCode code) {
        return repository.findByCode(code)
                .orElseGet(() -> {
                    throw new IllegalStateException(
                            "Missing achievement template in DB: " + code
                    );
                });
    }
}