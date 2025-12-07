package com.climbup.dto.response;

public class BadgeResponseDTO {

    private String name;
    private String description;
    private String icon; // emoji or image URL

    public BadgeResponseDTO() {}

    public BadgeResponseDTO(String name, String description, String icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}
