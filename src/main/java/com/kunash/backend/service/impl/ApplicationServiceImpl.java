package com.kunash.backend.service.impl;

import com.kunash.backend.dto.request.ApplicationRequest;
import com.kunash.backend.dto.response.ApplicationResponse;
import com.kunash.backend.entity.Application;
import com.kunash.backend.entity.Job;
import com.kunash.backend.exception.ResourceNotFoundException;
import com.kunash.backend.repository.ApplicationRepository;
import com.kunash.backend.service.ApplicationService;
import com.kunash.backend.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobService jobService;

    @Value("${file.upload.dir:uploads/resumes/}")
    private String uploadDir;

    // ==========================================
    // VALIDATE RESUME FILE - PDF ONLY
    // ==========================================
    private void validateResumeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Resume file is required");
        }

        // 1. Check file type - PDF ONLY
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        // PDF ONLY
        boolean isValidType =
                "application/pdf".equals(contentType) ||
                        extension.equals(".pdf");

        if (!isValidType) {
            throw new RuntimeException("Only PDF files are allowed. Uploaded: " + originalFilename);
        }

        // 2. Check file size (10KB - 5MB)
        long minSize = 10 * 1024;          // 10KB minimum
        long maxSize = 5 * 1024 * 1024;    // 5MB maximum

        if (file.getSize() < minSize) {
            throw new RuntimeException("Resume file is too small. Minimum size is 10KB.");
        }

        if (file.getSize() > maxSize) {
            throw new RuntimeException("Resume file is too large. Maximum size is 5MB.");
        }

        System.out.println("✅ Resume validation passed (PDF only): " + originalFilename + " (" + file.getSize() + " bytes)");
    }

    // ==========================================
    // HELPER: Save Resume File (PDF only)
    // ==========================================
    private String saveResumeFile(MultipartFile file) throws IOException {
        // Validate before saving
        validateResumeFile(file);

        // 1. Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 2. Generate unique filename (always .pdf)
        String originalFilename = file.getOriginalFilename();
        String newFileName = UUID.randomUUID().toString() + ".pdf";

        // 3. Save file to disk
        Path filePath = uploadPath.resolve(newFileName);
        Files.write(filePath, file.getBytes());

        // 4. Return the saved filename (to store in database)
        return uploadDir + newFileName;
    }

    // ==========================================
    // SUBMIT APPLICATION
    // ==========================================
    @Override
    public ApplicationResponse submitApplication(ApplicationRequest request) {
        try {
            // 1. Get the job from database
            Long jobId = Long.parseLong(request.getJobId());
            Job job = jobService.getJobEntityById(jobId);

            // 2. Check if job is active
            if (!"active".equals(job.getStatus())) {
                throw new RuntimeException("This job is no longer accepting applications");
            }

            // 3. Handle file upload with validation
            MultipartFile resumeFile = request.getResume();
            String savedFileName = saveResumeFile(resumeFile);

            // 4. Create Application entity
            Application application = new Application();
            application.setJob(job);
            application.setName(request.getName());
            application.setEmail(request.getEmail());
            application.setPhone(request.getPhone());
            application.setLocation(request.getLocation());
            application.setLinkedin(request.getLinkedin());
            application.setCoverMessage(request.getCoverMessage());
            application.setResumePath(savedFileName);
            application.setResumeOriginalName(resumeFile.getOriginalFilename());
            application.setStatus("new");
            application.setAppliedAt(LocalDateTime.now());

            // 5. Save to database
            Application savedApplication = applicationRepository.save(application);

            // 6. Convert to Response DTO
            return convertToResponse(savedApplication);

        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid job ID format");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save resume file: " + e.getMessage());
        }
    }

    // ==========================================
    // GET ALL APPLICATIONS (Admin)
    // ==========================================
    @Override
    public List<ApplicationResponse> getAllApplications() {
        List<Application> applications = applicationRepository.findAllByOrderByAppliedAtDesc();
        return applications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // GET APPLICATIONS BY JOB
    // ==========================================
    @Override
    public List<ApplicationResponse> getApplicationsByJob(Long jobId) {
        Job job = jobService.getJobEntityById(jobId);
        List<Application> applications = applicationRepository.findByJobOrderByAppliedAtDesc(job);
        return applications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // GET APPLICATIONS BY STATUS
    // ==========================================
    @Override
    public List<ApplicationResponse> getApplicationsByStatus(String status) {
        List<Application> applications = applicationRepository.findByStatus(status);
        return applications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // GET APPLICATION BY ID
    // ==========================================
    @Override
    public ApplicationResponse getApplicationById(Long id) {
        Application application = getApplicationEntityById(id);
        return convertToResponse(application);
    }

    // ==========================================
    // GET APPLICATION ENTITY (Internal use)
    // ==========================================
    @Override
    public Application getApplicationEntityById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
    }

    // ==========================================
    // UPDATE APPLICATION STATUS
    // ==========================================
    @Override
    public ApplicationResponse updateApplicationStatus(Long id, String status) {
        Application application = getApplicationEntityById(id);

        // Validate status
        if (!isValidStatus(status)) {
            throw new RuntimeException("Invalid status: " + status + ". Allowed: new, shortlisted, selected, rejected");
        }

        application.setStatus(status);
        Application updatedApplication = applicationRepository.save(application);
        return convertToResponse(updatedApplication);
    }

    // ==========================================
    // UPDATE APPLICATION NOTES
    // ==========================================
    @Override
    public ApplicationResponse updateApplicationNotes(Long id, String notes) {
        Application application = getApplicationEntityById(id);
        application.setNotes(notes);
        Application updatedApplication = applicationRepository.save(application);
        return convertToResponse(updatedApplication);
    }

    // ==========================================
    // DELETE APPLICATION
    // ==========================================
    @Override
    public void deleteApplication(Long id) {
        Application application = getApplicationEntityById(id);
        applicationRepository.delete(application);
    }

    // ==========================================
    // GET RESUME PATH
    // ==========================================
    @Override
    public String getResumePath(Long applicationId) {
        Application application = getApplicationEntityById(applicationId);
        return application.getResumePath();
    }

    // ==========================================
    // GET RECENT APPLICATIONS (Last 10)
    // ==========================================
    @Override
    public List<ApplicationResponse> getRecentApplications() {
        List<Application> applications = applicationRepository.findAllByOrderByAppliedAtDesc();
        // Get only first 10 (or less if fewer exist)
        int limit = Math.min(10, applications.size());
        return applications.stream()
                .limit(limit)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // GET APPLICATION STATISTICS BY POSITION
    // ==========================================
    @Override
    public Map<String, Long> getApplicationsByPositionStats() {
        List<Object[]> results = applicationRepository.countApplicationsByJobTitle();
        Map<String, Long> stats = new HashMap<>();
        for (Object[] result : results) {
            String jobTitle = (String) result[0];
            Long count = (Long) result[1];
            stats.put(jobTitle != null ? jobTitle : "Unknown Position", count);
        }
        return stats;
    }

    // ==========================================
    // HELPER: Validate Status
    // ==========================================
    private boolean isValidStatus(String status) {
        return status != null && (
                "new".equals(status) ||
                        "shortlisted".equals(status) ||
                        "selected".equals(status) ||
                        "rejected".equals(status)
        );
    }

    // ==========================================
    // HELPER: Convert Entity to Response DTO
    // ==========================================
    private ApplicationResponse convertToResponse(Application application) {
        return new ApplicationResponse(application);
    }
}