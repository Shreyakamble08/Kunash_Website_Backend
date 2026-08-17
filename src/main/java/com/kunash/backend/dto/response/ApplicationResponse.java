package com.kunash.backend.dto.response;

import com.kunash.backend.entity.Application;
import java.time.LocalDateTime;

public class ApplicationResponse {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private String name;
    private String email;
    private String phone;
    private String location;
    private String linkedin;
    private String coverMessage;
    private String resumePath;
    private String resumeOriginalName;
    private String status;
    private String notes;
    private LocalDateTime appliedAt;

    // ==========================================
    // CONSTRUCTORS
    // ==========================================
    public ApplicationResponse() {
    }

    public ApplicationResponse(Application application) {
        this.id = application.getId();
        this.jobId = application.getJob() != null ? application.getJob().getId() : null;
        this.jobTitle = application.getJob() != null ? application.getJob().getTitle() : null;
        this.name = application.getName();
        this.email = application.getEmail();
        this.phone = application.getPhone();
        this.location = application.getLocation();
        this.linkedin = application.getLinkedin();
        this.coverMessage = application.getCoverMessage();
        this.resumePath = application.getResumePath();
        this.resumeOriginalName = application.getResumeOriginalName();
        this.status = application.getStatus();
        this.notes = application.getNotes();
        this.appliedAt = application.getAppliedAt();
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

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getCoverMessage() {
        return coverMessage;
    }

    public void setCoverMessage(String coverMessage) {
        this.coverMessage = coverMessage;
    }

    public String getResumePath() {
        return resumePath;
    }

    public void setResumePath(String resumePath) {
        this.resumePath = resumePath;
    }

    public String getResumeOriginalName() {
        return resumeOriginalName;
    }

    public void setResumeOriginalName(String resumeOriginalName) {
        this.resumeOriginalName = resumeOriginalName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }
}