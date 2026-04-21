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

	private final UserAchievementRepository userAchievementRepository;
	private final AchievementTemplateRepository achievementTemplateRepository;
    private final AchievementTemplateService templateService;
    private final AchievementEvaluationService evaluationService;
    private final GoalRepository goalRepository;

    public AchievementService(UserAchievementRepository userAchievementRepository,
                              AchievementTemplateService templateService,
                              AchievementEvaluationService evaluationService,
                              AchievementTemplateRepository achievementTemplateRepository,
                              GoalRepository goalRepository) {
        this.userAchievementRepository = userAchievementRepository;
        this.templateService = templateService;
        this.evaluationService = evaluationService;
        this .achievementTemplateRepository = achievementTemplateRepository;
        this.goalRepository = goalRepository;
    }

    // 🔹 Initialize achievements for new user
    @Transactional
    public List<AchievementResponseDTO> initialize(User user) {

        if (userAchievementRepository.existsByUserId(user.getId())) {
            return getUserAchievements(user.getId());
        }

        List<UserAchievement> list = templateService.getAll().stream()
                .map(template -> {
                    UserAchievement ua = new UserAchievement();
                    ua.setUser(user);
                    ua.setTemplate(template);
                    ua.setUnlocked(false);
                    ua.setNewlyUnlocked(false);
                    ua.setSeen(false);
                    return ua;
                })
                .toList();

        return userAchievementRepository.saveAll(list)
                .stream()
                .map(AchievementMapper::toResponseDTO)
                .toList();
    }
    // 🔹 Trigger evaluation
    @Transactional
    public void evaluate(User user) {
        evaluationService.evaluate(user);
    }
    
    // 🔹 Get all achievements
       
    public List<AchievementResponseDTO> getUserAchievements(Long userId) {

        return userAchievementRepository
                .findAllByUserIdWithTemplate(userId)
                .stream()
                .map(ua -> {
                    AchievementResponseDTO dto = new AchievementResponseDTO();

                    dto.setId(ua.getId());
                    dto.setTitle(ua.getTemplate().getTitle());
                    dto.setDescription(ua.getTemplate().getDescription());
                    dto.setUnlocked(ua.isUnlocked());
                    dto.setNewlyUnlocked(ua.isNewlyUnlocked());

                    return dto;
                })
                .toList();
    }
    public Map<String, List<UserAchievement>> getAchievementsForUser(User user) {
        List<UserAchievement> customGoals = userAchievementRepository.findByUserAndGoalIsNotNull(user);
        List<UserAchievement> defaultTemplates = userAchievementRepository.findByUserAndGoalIsNull(user);

        Map<String, List<UserAchievement>> result = new HashMap<>();
        result.put("customGoals", customGoals);
        result.put("defaultTemplates", defaultTemplates);
        return result;
    }
    
    // 🔹 Mark achievements as seen
    @Transactional
    public void markSeen(User user) {
        List<UserAchievement> list =
                userAchievementRepository.findByUserAndNewlyUnlockedTrue(user);

        list.forEach(UserAchievement::markSeen);
        userAchievementRepository.saveAll(list);
    }

    // 🔹 Check new achievements
    public boolean hasNew(User user) {
    	return userAchievementRepository.existsByUserAndNewlyUnlockedTrue(user);
    }

    // 🔹 Check specific achievement
    public boolean hasAchievement(User user, AchievementTemplate template) {
        return userAchievementRepository.existsByUserAndTemplate(user, template);
    }
    
    @Transactional
    public void initUserAchievements(User user) {

        System.out.println("INIT for user: " + user.getId());

        List<AchievementTemplate> templates = achievementTemplateRepository.findAll();

        List<UserAchievement> existing =
                userAchievementRepository.findByUserId(user.getId());

        System.out.println("Existing: " + existing.size());
        System.out.println("Templates: " + templates.size());

        Set<Long> existingTemplateIds = existing.stream()
                .map(ua -> ua.getTemplate().getId())
                .collect(Collectors.toSet());

        List<UserAchievement> toSave = new ArrayList<>();

        for (AchievementTemplate t : templates) {
        	if (t.getCode() == AchievementCode.CUSTOM_GOAL) continue;
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
            userAchievementRepository.saveAll(toSave);
        }
    }
    
    public AchievementTemplate findTemplateByName(String name) {
        return achievementTemplateRepository.findByTitle(name)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement template not found: " + name));
    }
    
    public List<AchievementResponseDTO> getUserGoals(User user) {
        return userAchievementRepository.findByUserAndGoalIsNotNull(user)
                .stream()
                .map(AchievementMapper::toResponseDTO)
                .toList();
    }

    public List<AchievementResponseDTO> getTemplateGoals(User user) {
        return userAchievementRepository.findByUserAndGoalIsNull(user)
                .stream()
                .filter(ua -> ua.getTemplate().getCode() != AchievementCode.CUSTOM_GOAL)
                .map(AchievementMapper::toResponseDTO)
                .toList();
    }


    @Transactional
    public UserAchievement unlockByGoalId(Long goalId) {

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found: " + goalId));

        UserAchievement achievement =
                userAchievementRepository.findByGoal(goal)
                        .stream()
                        .findFirst()
                        .orElseThrow(() ->
                                new ResourceNotFoundException("No achievement linked to goal " + goalId));

        achievement.setUnlocked(true);
        achievement.setNewlyUnlocked(true);

        return userAchievementRepository.save(achievement);
    }

}