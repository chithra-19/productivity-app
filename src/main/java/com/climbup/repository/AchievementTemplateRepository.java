package com.climbup.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.climbup.model.AchievementCode;
import com.climbup.model.AchievementTemplate;

public interface AchievementTemplateRepository extends JpaRepository<AchievementTemplate, Long> {

    Optional<AchievementTemplate> findByCode(AchievementCode code);

    boolean existsByCode(AchievementCode code);
    
    Optional<AchievementTemplate> findByTitle(String title);
}