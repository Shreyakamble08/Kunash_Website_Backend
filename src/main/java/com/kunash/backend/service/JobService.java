package com.kunash.backend.service;

import com.kunash.backend.dto.request.JobRequest;
import com.kunash.backend.dto.response.JobResponse;
import com.kunash.backend.entity.Job;

import java.util.List;

public interface JobService {

    /**
     * Create a new job posting
     */
    JobResponse createJob(JobRequest request);

    /**
     * Get all jobs (for admin - includes closed jobs)
     */
    List<JobResponse> getAllJobs();

    /**
     * Get all ACTIVE jobs (for public website)
     */
    List<JobResponse> getActiveJobs();

    /**
     * Get a specific job by ID
     */
    JobResponse getJobById(Long id);

    /**
     * Get the actual Job entity (for internal use)
     */
    Job getJobEntityById(Long id);

    /**
     * Update an existing job
     */
    JobResponse updateJob(Long id, JobRequest request);

    /**
     * Delete a job (and all its applications)
     */
    void deleteJob(Long id);

    /**
     * Toggle job status (active ↔ closed)
     */
    JobResponse toggleJobStatus(Long id);

    /**
     * Get the count of applicants for a job
     */
    long getApplicantCount(Long jobId);
}