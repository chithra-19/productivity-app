package com.climbup.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "badges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

   
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    // Optional: type of condition
    @Enumerated(EnumType.STRING)
    private BadgeType badgeType;

    // Threshold value (example: 7 for streak, 100 for tasks)
    private Integer threshold;
    

	@Column(unique = true, nullable = false)
    private String code;

    
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

    public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getImageUrl() {
		return imageUrl;
	}


	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	public BadgeType getBadgeType() {
		return badgeType;
	}


	public void setBadgeType(BadgeType badgeType) {
		this.badgeType = badgeType;
	}


	public Integer getThreshold() {
		return threshold;
	}


	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}


    
   
}
