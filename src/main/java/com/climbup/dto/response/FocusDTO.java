package com.climbup.dto.response;

public class FocusDTO {
  
	private String sessionName;
    private String startTime;
    private String endTime;
    private int durationMinutes;
    private boolean successful;
    private String iconUrl;

    public String getSessionName() {
  		return sessionName;
  	}

  	public void setSessionName(String sessionName) {
  		this.sessionName = sessionName;
  	}

  	public String getStartTime() {
  		return startTime;
  	}

  	public void setStartTime(String startTime) {
  		this.startTime = startTime;
  	}

  	public String getEndTime() {
  		return endTime;
  	}

  	public void setEndTime(String endTime) {
  		this.endTime = endTime;
  	}

  	public int getDurationMinutes() {
  		return durationMinutes;
  	}

  	public void setDurationMinutes(int durationMinutes) {
  		this.durationMinutes = durationMinutes;
  	}

  	public boolean isSuccessful() {
  		return successful;
  	}

  	public void setSuccessful(boolean successful) {
  		this.successful = successful;
  	}

  	public String getIconUrl() {
  		return iconUrl;
  	}

  	public void setIconUrl(String iconUrl) {
  		this.iconUrl = iconUrl;
  	}

    
    public FocusDTO(String sessionName, String startTime, String endTime,
                    int durationMinutes, boolean successful, String iconUrl) {
        this.sessionName = sessionName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.successful = successful;
        this.iconUrl = iconUrl;
    }

    // Getters and setters
}