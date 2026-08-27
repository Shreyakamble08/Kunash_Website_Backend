package com.kunash.backend.service;

public interface EmailService {

    void sendEmail(String to, String subject, String body);

    void sendThankYouEmail(String userEmail, String userName, String formType);

    void sendAdminNotification(String formData, String formType, String userEmail);
}