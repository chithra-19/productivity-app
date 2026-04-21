package com.climbup.model;

import jakarta.persistence.*;

@Entity
@Table(name = "achievement_templates")
public class AchievementTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", unique = true)
    private AchievementCode code;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AchievementType type;
  

    @Column(nullable = false)
    private String title;

    private String description;
    private String icon;
    
  
	@Column
    private String metric; // GOALS, TASKS, STREAK, XP, EARLY, NIGHT

    @Column
    private Integer threshold;


    private String category;

    // getters & setters

    public Long getId() { return id; }

    public AchievementCode getCode() { return code; }
    public void setCode(AchievementCode code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public AchievementType getType() { return type; }
    public void setType(AchievementType type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getMetric() {
  		return metric;
  	}

  	public Integer getThreshold() {
  		return threshold;
  	}

  	public void setId(Long id) {
  		this.id = id;
  	}

  	public void setMetric(String metric) {
  		this.metric = metric;
  	}

  	public void setThreshold(Integer threshold) {
  		this.threshold = threshold;
  	}
}