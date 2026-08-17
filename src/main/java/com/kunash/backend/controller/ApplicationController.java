package com.kunash.backend.controller;

import com.kunash.backend.dto.request.ApplicationRequest;
import com.kunash.backend.dto.response.ApiResponse;
import com.kunash.backend.dto.response.ApplicationResponse;
import com.kunash.backend.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    // ==========================================
    // PUBLIC API: SUBMIT APPLICATION
    // ==========================================
    @PostMapping(value = "/applications", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ApplicationResponse>> submitApplication(
            @Valid @ModelAttribute ApplicationRequest request) {
        ApplicationResponse application = applicationService.submitApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Application submitted successfully", application));
    }

    // ==========================================
    // ADMIN APIs: GET ALL APPLICATIONS
    // ==========================================
    @GetMapping("/admin/applications")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getAllApplications() {
        List<ApplicationResponse> applications = applicationService.getAllApplications();
        return ResponseEntity.ok(
                ApiResponse.success("Applications retrieved successfully", applications)
        );
    }

    // ==========================================
    // ADMIN API: GET RECENT APPLICATIONS (Last 10)
    // ==========================================
    @GetMapping("/admin/applications/recent")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getRecentApplications() {
        List<ApplicationResponse> applications = applicationService.getRecentApplications();
        return ResponseEntity.ok(
                ApiResponse.success("Recent applications retrieved successfully", applications)
        );
    }

    // ==========================================
    // ADMIN API: GET APPLICATION STATS BY POSITION
    // ==========================================
    @GetMapping("/admin/applications/stats/by-position")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getApplicationsByPositionStats() {
        Map<String, Long> stats = applicationService.getApplicationsByPositionStats();
        return ResponseEntity.ok(
                ApiResponse.success("Application statistics retrieved successfully", stats)
        );
    }

    // ==========================================
    // ADMIN API: GET APPLICATIONS BY JOB
    // ==========================================
    @GetMapping("/admin/applications/job/{jobId}")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getApplicationsByJob(
            @PathVariable Long jobId) {
        List<ApplicationResponse> applications = applicationService.getApplicationsByJob(jobId);
        return ResponseEntity.ok(
                ApiResponse.success("Applications retrieved successfully", applications)
        );
    }

    // ==========================================
    // ADMIN API: GET APPLICATIONS BY STATUS
    // ==========================================
    @GetMapping("/admin/applications/status/{status}")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getApplicationsByStatus(
            @PathVariable String status) {
        List<ApplicationResponse> applications = applicationService.getApplicationsByStatus(status);
        return ResponseEntity.ok(
                ApiResponse.success("Applications retrieved successfully", applications)
        );
    }

    // ==========================================
    // ADMIN API: GET APPLICATION BY ID
    // ==========================================
    @GetMapping("/admin/applications/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplicationById(
            @PathVariable Long id) {
        ApplicationResponse application = applicationService.getApplicationById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Application retrieved successfully", application)
        );
    }

    // ==========================================
    // ADMIN API: UPDATE APPLICATION STATUS
    // ==========================================
    @PatchMapping("/admin/applications/{id}/status")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        ApplicationResponse application = applicationService.updateApplicationStatus(id, status);
        return ResponseEntity.ok(
                ApiResponse.success("Application status updated successfully", application)
        );
    }

    // ==========================================
    // ADMIN API: UPDATE APPLICATION NOTES
    // ==========================================
    @PatchMapping("/admin/applications/{id}/notes")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateApplicationNotes(
            @PathVariable Long id,
            @RequestParam String notes) {
        ApplicationResponse application = applicationService.updateApplicationNotes(id, notes);
        return ResponseEntity.ok(
                ApiResponse.success("Application notes updated successfully", application)
        );
    }

    // ==========================================
    // ADMIN API: DELETE APPLICATION
    // ==========================================
    @DeleteMapping("/admin/applications/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.ok(
                ApiResponse.success("Application deleted successfully")
        );
    }

    // ==========================================
    // ADMIN API: DOWNLOAD RESUME (PDF ONLY - FIXED)
    // ==========================================
    @GetMapping("/admin/applications/{id}/resume")
    public ResponseEntity<Resource> downloadResume(@PathVariable Long id) {
        try {
            // 1. Get the application details
            ApplicationResponse application = applicationService.getApplicationById(id);

            // 2. Get the resume file path
            String resumePath = applicationService.getResumePath(id);
            Path filePath = Paths.get(resumePath);
            File file = filePath.toFile();

            // 3. Check if file exists
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            // 4. Get the original filename from database
            String originalFileName = application.getResumeOriginalName();
            if (originalFileName == null || originalFileName.isEmpty()) {
                originalFileName = file.getName();
            }

            // 5. Ensure filename ends with .pdf
            if (!originalFileName.toLowerCase().endsWith(".pdf")) {
                String baseName = originalFileName;
                if (baseName.contains(".")) {
                    baseName = baseName.substring(0, baseName.lastIndexOf("."));
                }
                originalFileName = baseName + ".pdf";
            }

            // 6. Log the download details
            System.out.println("📥 Downloading resume (PDF only):");
            System.out.println("   Original filename: " + originalFileName);
            System.out.println("   File path: " + filePath);
            System.out.println("   File size: " + file.length() + " bytes");

            // 7. Create resource
            Resource resource = new FileSystemResource(file);

            // 8. Return file with PDF headers
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + originalFileName + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .contentLength(file.length())
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}