package com.kunash.backend.controller;

import com.kunash.backend.dto.request.JobRequest;
import com.kunash.backend.dto.response.ApiResponse;
import com.kunash.backend.dto.response.JobResponse;
import com.kunash.backend.service.JobService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    // ==========================================
    // 1. CREATE JOB - POST /api/admin/jobs
    // ==========================================
    @PostMapping
    public ResponseEntity<ApiResponse<JobResponse>> createJob(@Valid @RequestBody JobRequest request) {
        JobResponse createdJob = jobService.createJob(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Job created successfully", createdJob));
    }

    // ==========================================
    // 2. GET ALL JOBS - GET /api/admin/jobs
    // ==========================================
    @GetMapping
    public ResponseEntity<ApiResponse<List<JobResponse>>> getAllJobs() {
        List<JobResponse> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(
                ApiResponse.success("Jobs retrieved successfully", jobs)
        );
    }

    // ==========================================
    // 3. GET JOB BY ID - GET /api/admin/jobs/{id}
    // ==========================================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable Long id) {
        JobResponse job = jobService.getJobById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Job retrieved successfully", job)
        );
    }

    // ==========================================
    // 4. UPDATE JOB - PUT /api/admin/jobs/{id}
    // ==========================================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request) {
        JobResponse updatedJob = jobService.updateJob(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Job updated successfully", updatedJob)
        );
    }

    // ==========================================
    // 5. DELETE JOB - DELETE /api/admin/jobs/{id}
    // ==========================================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.ok(
                ApiResponse.success("Job deleted successfully")
        );
    }

    // ==========================================
    // 6. TOGGLE JOB STATUS - PATCH /api/admin/jobs/{id}/status
    // ==========================================
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<JobResponse>> toggleJobStatus(@PathVariable Long id) {
        JobResponse job = jobService.toggleJobStatus(id);
        return ResponseEntity.ok(
                ApiResponse.success("Job status toggled successfully", job)
        );
    }
}