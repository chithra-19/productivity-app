package com.climbup.service.productivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.exception.ResourceNotFoundException;
import com.climbup.mapper.AchievementMapper;
import com.climbup.model.*;
import com.climbup.repository.*;

@Service
public class AchievementService {

	private final UserAchievementRepository achievementRepository;
	private final AchievementTemplateRepository achievementTemplateRepository;
    private final AchievementTemplateService templateService;
    private final AchievementEvaluationService evaluationService;

    public AchievementService(UserAchievementRepository achievementRepository,
                              AchievementTemplateService templateService,
                              AchievementEvaluationService evaluationService,
                              AchievementTemplateRepository achievementTemplateRepository) {
        this.achievementRepository = achievementRepository;
        this.templateService = templateService;
        this.evaluationService = evaluationService;
        this .achievementTemplateRepository = achievementTemplateRepository;
    }

    // 🔹 Initialize achievements for new user
    @Transactional
    public List<AchievementResponseDTO> initialize(User user) {

    	if (!achievementRepository.findByUserId(user.getId()).isEmpty()){
            return getUserAchievements(user);
        }

        List<UserAchievement> list = templateService.getAll().stream()
                .map(template -> {
                    UserAchievement ua = new UserAchievement();
                    ua.setUser(user);
                    ua.setTemplate(template);
                    return ua;
                })
                .toList();

        return achievementRepository.saveAll(list)
                .stream()
                .map(AchievementMapper::toResponseDTO)
                .toList();
    }

    // 🔹 Trigger evaluation
    @Transactional
    public void evaluate(User user) {
    	  initUserAchievements(user);
        evaluationService.evaluate(user);
    }

    // 🔹 Get all achievements
    public List<AchievementResponseDTO> getUserAchievements(User user) {

        initUserAchievements(user);

        return achievementRepository.findByUserIdWithTemplate(user.getId())
                .stream()
                .map(AchievementMapper::toResponseDTO)
                .toList();
    }
    
    public Map<String, List<UserAchievement>> getAchievementsForUser(User user) {
        List<UserAchievement> customGoals = achievementRepository.findByUserAndGoalIsNotNull(user);
        List<UserAchievement> defaultTemplates = achievementRepository.findByUserAndGoalIsNull(user);

        Map<String, List<UserAchievement>> result = new HashMap<>();
        result.put("customGoals", customGoals);
        result.put("defaultTemplates", defaultTemplates);
        return result;
    }
    
    // 🔹 Mark achievements as seen
    @Transactional
    public void markSeen(User user) {
        List<UserAchievement> list =
                achievementRepository.findByUserAndNewlyUnlockedTrue(user);

        list.forEach(UserAchievement::markSeen);
        achievementRepository.saveAll(list);
    }

    // 🔹 Check new achievements
    public boolean hasNew(User user) {
    	return achievementRepository.existsByUserAndNewlyUnlockedTrue(user);
    }

    // 🔹 Check specific achievement
    public boolean hasAchievement(User user, AchievementTemplate template) {
        return achievementRepository.existsByUserAndTemplate(user, template);
    }
    
    @Transactional
    public void initUserAchievements(User user) {

        System.out.println("INIT for user: " + user.getId());

        List<AchievementTemplate> templates = achievementTemplateRepository.findAll();

        List<UserAchievement> existing =
                achievementRepository.findByUserId(user.getId());

        System.out.println("Existing: " + existing.size());
        System.out.println("Templates: " + templates.size());

        Set<Long> existingTemplateIds = existing.stream()
                .map(ua -> ua.getTemplate().getId())
                .collect(Collectors.toSet());

        List<UserAchievement> toSave = new ArrayList<>();

        for (AchievementTemplate t : templates) {
            if (!existingTemplateIds.contains(t.getId())) {
                UserAchievement ua = new UserAchievement();
                ua.setUser(user);
                ua.setTemplate(t);
                ua.setUnlocked(false);
                ua.setNewlyUnlocked(false);
                ua.setSeen(false);
                toSave.add(ua);
            }
        }

        System.out.println("TO SAVE: " + toSave.size());

        if (!toSave.isEmpty()) {
            achievementRepository.saveAll(toSave);
        }
    }
    
    public AchievementTemplate findTemplateByName(String name) {
        return achievementTemplateRepository.findByTitle(name)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement template not found: " + name));
    }
    
    public List<AchievementResponseDTO> getUserGoals(User user) {
        return achievementRepository.findByUserAndGoalIsNotNull(user)
            .stream()
            .map(AchievementResponseDTO::fromEntity)
            .toList();
    }

    public List<AchievementResponseDTO> getTemplateGoals(User user) {
        return achievementRepository.findByUserAndGoalIsNull(user)
            .stream()
            .map(AchievementResponseDTO::fromEntity)
            .toList();
    }

}