package com.kunash.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class JobRequest {

    @NotBlank(message = "Job title is required")
    @Size(max = 255, message = "Job title cannot exceed 255 characters")
    private String title;

    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location cannot exceed 255 characters")
    private String location;

    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;

    // ==========================================
    // CONSTRUCTORS
    // ==========================================
    public JobRequest() {
    }

    public JobRequest(String title, String location, String description) {
        this.title = title;
        this.location = location;
        this.description = description;
    }

    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}