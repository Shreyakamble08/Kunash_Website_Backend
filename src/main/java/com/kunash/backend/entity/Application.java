package com.kunash.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 50)
    private String phone;

    @Column(length = 255)
    private String location;

    @Column(length = 500)
    private String linkedin;

    @Column(name = "cover_message", columnDefinition = "TEXT")
    private String coverMessage;

    @Column(name = "resume_path", nullable = false, length = 500)
    private String resumePath;

    @Column(name = "resume_original_name", nullable = false, length = 255)
    private String resumeOriginalName;

    @Column(length = 20)
    private String status = "new";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    // ==========================================
    // CONSTRUCTORS
    // ==========================================
    public Application() {
        // Empty constructor required by JPA
    }

    public Application(Job job, String name, String email, String phone,
                       String resumePath, String resumeOriginalName) {
        this.job = job;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.resumePath = resumePath;
        this.resumeOriginalName = resumeOriginalName;
        this.status = "new";
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

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
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