package com.kunash.backend.dto.response;

import com.kunash.backend.entity.Job;
import java.time.LocalDateTime;

public class JobResponse {

    private Long id;
    private String title;
    private String location;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int applicantCount;  // Extra field: count of applicants

    // ==========================================
    // CONSTRUCTORS
    // ==========================================
    public JobResponse() {
    }

    public JobResponse(Job job) {
        this.id = job.getId();
        this.title = job.getTitle();
        this.location = job.getLocation();
        this.description = job.getDescription();
        this.status = job.getStatus();
        this.createdAt = job.getCreatedAt();
        this.updatedAt = job.getUpdatedAt();
        this.applicantCount = 0;  // Will be set separately
    }

    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getApplicantCount() {
        return applicantCount;
    }

    public void setApplicantCount(int applicantCount) {
        this.applicantCount = applicantCount;
    }
}