package com.climbup.dto.response;

public class UserResponseDTO {
    private Long id;
    private String firstName;
 
	private String email;

    // ✅ Add this constructor
    public UserResponseDTO(Long id, String firstName,  String email) {
        this.id = id;
        this.firstName = firstName;
        this.email = email;
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFirstName() {
 		return firstName;
 	}

 	public void setFirstName(String firstName) {
 		this.firstName = firstName;
 	}

    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
