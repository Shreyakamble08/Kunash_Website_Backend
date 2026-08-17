package com.kunash.backend.service.impl;

import com.kunash.backend.dto.request.JobRequest;
import com.kunash.backend.dto.response.JobResponse;
import com.kunash.backend.entity.Job;
import com.kunash.backend.exception.ResourceNotFoundException;
import com.kunash.backend.repository.ApplicationRepository;
import com.kunash.backend.repository.JobRepository;
import com.kunash.backend.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;  // ← ADDED THIS

    // ==========================================
    // CREATE JOB
    // ==========================================
    @Override
    public JobResponse createJob(JobRequest request) {
        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setLocation(request.getLocation());
        job.setDescription(request.getDescription());
        job.setStatus("active");
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());

        Job savedJob = jobRepository.save(job);
        return convertToResponse(savedJob);
    }

    // ==========================================
    // GET ALL JOBS (Admin)
    // ==========================================
    @Override
    public List<JobResponse> getAllJobs() {
        List<Job> jobs = jobRepository.findAllByOrderByCreatedAtDesc();
        return jobs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // GET ACTIVE JOBS (Public Website)
    // ==========================================
    @Override
    public List<JobResponse> getActiveJobs() {
        List<Job> jobs = jobRepository.findByStatus("active");
        return jobs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // GET JOB BY ID
    // ==========================================
    @Override
    public JobResponse getJobById(Long id) {
        Job job = getJobEntityById(id);
        return convertToResponse(job);
    }

    // ==========================================
    // GET JOB ENTITY (Internal use)
    // ==========================================
    @Override
    public Job getJobEntityById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
    }

    // ==========================================
    // UPDATE JOB
    // ==========================================
    @Override
    public JobResponse updateJob(Long id, JobRequest request) {
        Job job = getJobEntityById(id);

        job.setTitle(request.getTitle());
        job.setLocation(request.getLocation());
        job.setDescription(request.getDescription());
        job.setUpdatedAt(LocalDateTime.now());

        Job updatedJob = jobRepository.save(job);
        return convertToResponse(updatedJob);
    }

    // ==========================================
    // DELETE JOB
    // ==========================================
    @Override
    public void deleteJob(Long id) {
        Job job = getJobEntityById(id);
        jobRepository.delete(job);
    }

    // ==========================================
    // TOGGLE JOB STATUS
    // ==========================================
    @Override
    public JobResponse toggleJobStatus(Long id) {
        Job job = getJobEntityById(id);

        if ("active".equals(job.getStatus())) {
            job.setStatus("closed");
        } else {
            job.setStatus("active");
        }

        job.setUpdatedAt(LocalDateTime.now());

        Job updatedJob = jobRepository.save(job);
        return convertToResponse(updatedJob);
    }

    // ==========================================
    // GET APPLICANT COUNT (IMPLEMENTED!)
    // ==========================================
    @Override
    public long getApplicantCount(Long jobId) {
        Job job = getJobEntityById(jobId);
        return applicationRepository.countByJob(job);
    }

    // ==========================================
    // HELPER: Convert Entity to Response DTO (UPDATED!)
    // ==========================================
    private JobResponse convertToResponse(Job job) {
        JobResponse response = new JobResponse(job);

        // ✅ FIXED: Get actual count of applications for this job
        long count = applicationRepository.countByJob(job);
        response.setApplicantCount((int) count);

        return response;
    }
}