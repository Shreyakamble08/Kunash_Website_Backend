package com.kunash.backend.service;

import com.kunash.backend.dto.request.ApplicationRequest;
import com.kunash.backend.dto.response.ApplicationResponse;
import com.kunash.backend.entity.Application;

import java.util.List;
import java.util.Map;

public interface ApplicationService {

    /**
     * Submit a new job application (with resume upload)
     */
    ApplicationResponse submitApplication(ApplicationRequest request);

    /**
     * Get all applications (for admin)
     */
    List<ApplicationResponse> getAllApplications();

    /**
     * Get applications for a specific job
     */
    List<ApplicationResponse> getApplicationsByJob(Long jobId);

    /**
     * Get applications with a specific status
     */
    List<ApplicationResponse> getApplicationsByStatus(String status);

    /**
     * Get a specific application by ID
     */
    ApplicationResponse getApplicationById(Long id);

    /**
     * Get the actual Application entity (for internal use)
     */
    Application getApplicationEntityById(Long id);

    /**
     * Update application status (new → shortlisted → selected → rejected)
     */
    ApplicationResponse updateApplicationStatus(Long id, String status);

    /**
     * Update application notes (internal HR notes)
     */
    ApplicationResponse updateApplicationNotes(Long id, String notes);

    /**
     * Delete an application
     */
    void deleteApplication(Long id);

    /**
     * Get the resume file path for downloading
     */
    String getResumePath(Long applicationId);

    /**
     * Get recent applications (last 10)
     */
    List<ApplicationResponse> getRecentApplications();

    /**
     * Get application statistics by position
     */
    Map<String, Long> getApplicationsByPositionStats();
}