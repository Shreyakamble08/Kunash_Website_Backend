package com.kunash.backend.service.impl;

import com.kunash.backend.dto.request.ApplicationRequest;
import com.kunash.backend.dto.response.ApplicationResponse;
import com.kunash.backend.entity.Application;
import com.kunash.backend.entity.Job;
import com.kunash.backend.exception.ResourceNotFoundException;
import com.kunash.backend.repository.ApplicationRepository;
import com.kunash.backend.service.ApplicationService;
import com.kunash.backend.service.EmailService;
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

    @Autowired
    private EmailService emailService;  // ← Add this

    @Value("${file.upload.dir:uploads/resumes/}")
    private String uploadDir;

    private void validateResumeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Resume file is required");
        }

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        boolean isValidType = "application/pdf".equals(contentType) || extension.equals(".pdf");

        if (!isValidType) {
            throw new RuntimeException("Only PDF files are allowed. Uploaded: " + originalFilename);
        }

        long minSize = 10 * 1024;
        long maxSize = 5 * 1024 * 1024;

        if (file.getSize() < minSize) {
            throw new RuntimeException("Resume file is too small. Minimum size is 10KB.");
        }

        if (file.getSize() > maxSize) {
            throw new RuntimeException("Resume file is too large. Maximum size is 5MB.");
        }
    }

    private String saveResumeFile(MultipartFile file) throws IOException {
        validateResumeFile(file);

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String newFileName = UUID.randomUUID().toString() + ".pdf";
        Path filePath = uploadPath.resolve(newFileName);
        Files.write(filePath, file.getBytes());

        return uploadDir + newFileName;
    }

    @Override
    public ApplicationResponse submitApplication(ApplicationRequest request) {
        try {
            Long jobId = Long.parseLong(request.getJobId());
            Job job = jobService.getJobEntityById(jobId);

            if (!"active".equals(job.getStatus())) {
                throw new RuntimeException("This job is no longer accepting applications");
            }

            MultipartFile resumeFile = request.getResume();
            String savedFileName = saveResumeFile(resumeFile);

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

            Application savedApplication = applicationRepository.save(application);

            // ========== SEND EMAILS ==========
            try {
                String formType = "Job Application for " + job.getTitle();
                emailService.sendThankYouEmail(
                        savedApplication.getEmail(),
                        savedApplication.getName(),
                        formType
                );
            } catch (Exception e) {
                System.err.println("Failed to send thank you email: " + e.getMessage());
            }

            try {
                String formData = String.format(
                        "Job Title: %s\nApplicant: %s\nEmail: %s\nPhone: %s\nLocation: %s\nLinkedIn: %s\nCover Message: %s\nResume: %s\nApplied: %s",
                        job.getTitle(),
                        savedApplication.getName(),
                        savedApplication.getEmail(),
                        savedApplication.getPhone(),
                        savedApplication.getLocation() != null ? savedApplication.getLocation() : "N/A",
                        savedApplication.getLinkedin() != null ? savedApplication.getLinkedin() : "N/A",
                        savedApplication.getCoverMessage() != null ? savedApplication.getCoverMessage() : "N/A",
                        savedApplication.getResumeOriginalName(),
                        savedApplication.getAppliedAt()
                );
                emailService.sendAdminNotification(formData, "Job Application - " + job.getTitle(), savedApplication.getEmail());
            } catch (Exception e) {
                System.err.println("Failed to send admin notification: " + e.getMessage());
            }

            return convertToResponse(savedApplication);

        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid job ID format");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save resume file: " + e.getMessage());
        }
    }

    @Override
    public List<ApplicationResponse> getAllApplications() {
        List<Application> applications = applicationRepository.findAllByOrderByAppliedAtDesc();
        return applications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationResponse> getApplicationsByJob(Long jobId) {
        Job job = jobService.getJobEntityById(jobId);
        List<Application> applications = applicationRepository.findByJobOrderByAppliedAtDesc(job);
        return applications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationResponse> getApplicationsByStatus(String status) {
        List<Application> applications = applicationRepository.findByStatus(status);
        return applications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationResponse getApplicationById(Long id) {
        Application application = getApplicationEntityById(id);
        return convertToResponse(application);
    }

    @Override
    public Application getApplicationEntityById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
    }

    @Override
    public ApplicationResponse updateApplicationStatus(Long id, String status) {
        Application application = getApplicationEntityById(id);

        if (!isValidStatus(status)) {
            throw new RuntimeException("Invalid status: " + status + ". Allowed: new, shortlisted, selected, rejected");
        }

        application.setStatus(status);
        Application updatedApplication = applicationRepository.save(application);
        return convertToResponse(updatedApplication);
    }

    @Override
    public ApplicationResponse updateApplicationNotes(Long id, String notes) {
        Application application = getApplicationEntityById(id);
        application.setNotes(notes);
        Application updatedApplication = applicationRepository.save(application);
        return convertToResponse(updatedApplication);
    }

    @Override
    public void deleteApplication(Long id) {
        Application application = getApplicationEntityById(id);
        applicationRepository.delete(application);
    }

    @Override
    public String getResumePath(Long applicationId) {
        Application application = getApplicationEntityById(applicationId);
        return application.getResumePath();
    }

    @Override
    public List<ApplicationResponse> getRecentApplications() {
        List<Application> applications = applicationRepository.findAllByOrderByAppliedAtDesc();
        int limit = Math.min(10, applications.size());
        return applications.stream()
                .limit(limit)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

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

    private boolean isValidStatus(String status) {
        return status != null && (
                "new".equals(status) ||
                        "shortlisted".equals(status) ||
                        "selected".equals(status) ||
                        "rejected".equals(status)
        );
    }

    private ApplicationResponse convertToResponse(Application application) {
        return new ApplicationResponse(application);
    }
}