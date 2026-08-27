package com.kunash.backend.service.impl;

import com.kunash.backend.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${app.from.email}")
    private String fromEmail;

    @Value("${app.admin.email}")
    private String adminEmail;

    // local logo file inside src/main/resources — embedded inline in the email (works for any recipient, no hosting needed)
    @Value("${app.company.logo-path:static/Images/kunash-logo.png}")
    private String companyLogoPath;

    // fallback: public/CDN hosted image URL, only used if companyLogoPath file isn't found
    @Value("${app.company.logo-url:}")
    private String companyLogoUrl;


    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // logo resolve: pehle local classpath file try karo (embed inline), warna configured URL use karo
    private ClassPathResource resolveLogoResource() {
        if (companyLogoPath != null && !companyLogoPath.trim().isEmpty()) {
            ClassPathResource candidate = new ClassPathResource(companyLogoPath);
            if (candidate.exists()) {
                return candidate;
            }
        }
        return null;
    }

    private String resolveLogoSrc(ClassPathResource logoResource) {
        if (logoResource != null) {
            return "cid:companyLogo";
        }
        if (companyLogoUrl != null && !companyLogoUrl.trim().isEmpty()) {
            return companyLogoUrl;
        }
        return null;
    }

//    @Override
//    public void sendEmail(String to, String subject, String body) {
//        try {
//            log.info("📧 Sending email to: {}", to);
//            log.info("   Subject: {}", subject);
//
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            helper.setFrom(fromEmail);
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(body, true);
//
//            mailSender.send(message);
//            log.info("✅ Email sent successfully to: {}", to);
//
//        } catch (MessagingException e) {
//            log.error("❌ Email failed: {}", e.getMessage());
//            throw new RuntimeException("Failed to send email to: " + to, e);
//        }
//    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            log.info("📧 Sending email to: {}", to);
            log.info("   Subject: {}", subject);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            ClassPathResource logoResource = resolveLogoResource();
            String logoSrc = resolveLogoSrc(logoResource);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body.replace("{{LOGO_SRC}}", logoSrc != null ? logoSrc : ""), true);

            if (logoResource != null) {
                helper.addInline("companyLogo", logoResource);
            }

            mailSender.send(message);
            log.info("✅ Email sent successfully to: {}", to);

        } catch (MessagingException e) {
            log.error("❌ Email failed: {}", e.getMessage());
            throw new RuntimeException("Failed to send email to: " + to, e);
        }
    }

    @Override
    public void sendThankYouEmail(String userEmail, String userName, String formType) {
        try {
            log.info("Sending thank you email to: {}", userEmail);
            String body = buildThankYouEmail(userName, formType);
            String subject = "Thank You for Your " + formType + " Submission";
            sendEmail(userEmail, subject, body);
            log.info("✅ Thank you email sent to: {}", userEmail);
        } catch (Exception e) {
            log.error("❌ Failed to send thank you email: {}", e.getMessage());
        }
    }

    @Override
    public void sendAdminNotification(String formData, String formType, String userEmail) {
        try {
            log.info("Sending admin notification for: {}", formType);
            String body = buildAdminNotification(formData, formType, userEmail);
            String subject = "New " + formType + " Form Submission from " + userEmail;
            sendEmail(adminEmail, subject, body);
            log.info("✅ Admin notification sent");
        } catch (Exception e) {
            log.error("❌ Failed to send admin notification: {}", e.getMessage());
        }
    }

    // ==========================================
    // BUILD STATIC HTML EMAILS
    // ==========================================

    private String buildThankYouEmail(String userName, String formType) {
        String currentYear = String.valueOf(LocalDateTime.now().getYear());

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Thank You</title>\n" +
                "    <style>\n" +
                "        * { margin:0; padding:0; box-sizing:border-box; }\n" +
                "        body { margin:0; padding:0; background:#f7f5f3; font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Arial,sans-serif; -webkit-font-smoothing:antialiased; }\n" +
                "        .wrapper { width:100%; max-width:100%; background:#f7f5f3; padding:24px 12px; }\n" +
                "        .container { max-width:480px; margin:0 auto; background:#ffffff; border-radius:8px; overflow:hidden; box-shadow:0 2px 12px rgba(0,0,0,0.04); border:1px solid #efebe8; }\n" +
                "        .header { background:linear-gradient(135deg, #fef8f4 0%, #fdf0e8 100%); padding:18px 24px 14px; text-align:center; border-bottom:2px solid #FF6B35; }\n" +
                "        .header img { max-height:34px; width:auto; display:inline-block; }\n" +
                "        .header h1 { color:#1a1a1a; font-size:20px; font-weight:600; margin:6px 0 2px; letter-spacing:-0.2px; }\n" +
                "        .header .subtitle { color:#888; font-size:12px; margin:0; font-weight:400; }\n" +
                "        .content { padding:18px 22px 14px; color:#333; }\n" +
                "        .greeting { font-size:14px; margin:0 0 8px; color:#1a1a1a; }\n" +
                "        .greeting strong { color:#1a1a1a; }\n" +
                "        .message { font-size:13px; line-height:1.6; color:#555; margin:0 0 14px; }\n" +
                "        .highlight { background:#fef8f4; border-left:3px solid #FF6B35; border-radius:4px; padding:12px 14px; margin:0 0 16px; }\n" +
                "        .highlight .label { font-size:12px; font-weight:600; color:#FF6B35; margin:0 0 4px; letter-spacing:0.3px; }\n" +
                "        .highlight p { font-size:13px; color:#333; margin:0 0 2px; line-height:1.5; }\n" +
                "        .highlight .small { font-size:11px; color:#888; margin:4px 0 0; }\n" +
                "        .contact { font-size:12px; color:#666; margin:0 0 12px; }\n" +
                "        .contact a { color:#FF6B35; text-decoration:none; font-weight:500; }\n" +
                "        .signature { font-size:13px; color:#555; margin:0; line-height:1.6; }\n" +
                "        .signature strong { color:#1a1a1a; font-size:14px; }\n" +
                "        .footer { background:#faf8f6; padding:12px 22px; text-align:center; border-top:1px solid #f0ece8; }\n" +
                "        .footer .brand { font-size:13px; font-weight:600; color:#222; margin:0 0 2px; }\n" +
                "        .footer .brand span { color:#FF6B35; }\n" +
                "        .footer p { margin:1px 0; color:#aaa; font-size:10px; }\n" +
                "        @media (max-width:480px) { .wrapper { padding:12px 8px; } .header { padding:14px 16px 10px; } .header h1 { font-size:18px; } .header img { max-height:28px; } .content { padding:14px 16px 10px; } .highlight { padding:10px 12px; } .footer { padding:10px 16px; } }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"wrapper\">\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <img src=\"{{LOGO_SRC}}\" alt=\"Kunash Logo\">\n" +
                "            <h1>Thank You</h1>\n" +
                "            <p class=\"subtitle\">We appreciate your interest</p>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p class=\"greeting\">Dear <strong>" + escapeHtml(userName) + "</strong>,</p>\n" +
                "            <p class=\"message\">Thank you for submitting your <strong>" + escapeHtml(formType) + "</strong> inquiry. We've received your submission and will review it shortly.</p>\n" +
                "            <div class=\"highlight\">\n" +
                "                <p class=\"label\">" + iconNext() + " What happens next?</p>\n" +
                "                <p>Our team will review and respond within <strong>24–48 hours</strong>.</p>\n" +
                "                <p class=\"small\">We'll reply to the email you provided.</p>\n" +
                "            </div>\n" +
                "            <p class=\"contact\">" + iconMail() + " For urgent inquiries, <a href=\"mailto:kunashmedia@gmail.com\">contact us</a>.</p>\n" +
                "            <p class=\"signature\">Best regards,<br><strong>Kunash Team</strong></p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p class=\"brand\">Kunash<span>.</span></p>\n" +
                "            <p>Automated message — please do not reply</p>\n" +
                "            <p>&copy; " + currentYear + " Kunash. All rights reserved.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
    }

    private String buildAdminNotification(String formData, String formType, String userEmail) {
        String currentYear = String.valueOf(LocalDateTime.now().getYear());
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>New Submission</title>\n" +
                "    <style>\n" +
                "        * { margin:0; padding:0; box-sizing:border-box; }\n" +
                "        body { margin:0; padding:0; background:#f7f5f3; font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Arial,sans-serif; -webkit-font-smoothing:antialiased; }\n" +
                "        .wrapper { width:100%; max-width:100%; background:#f7f5f3; padding:24px 12px; }\n" +
                "        .container { max-width:520px; margin:0 auto; background:#ffffff; border-radius:8px; overflow:hidden; box-shadow:0 2px 12px rgba(0,0,0,0.04); border:1px solid #efebe8; }\n" +
                "        .header { background:linear-gradient(135deg, #fef8f4 0%, #fdf0e8 100%); padding:16px 24px 12px; text-align:center; border-bottom:2px solid #FF6B35; }\n" +
                "        .header img { max-height:32px; width:auto; display:inline-block; }\n" +
                "        .header h1 { color:#1a1a1a; font-size:18px; font-weight:600; margin:4px 0 2px; letter-spacing:-0.2px; }\n" +
                "        .badge { display:inline-block; background:#FF6B35; color:#fff; padding:2px 14px; border-radius:12px; font-size:10px; font-weight:600; text-transform:uppercase; letter-spacing:0.3px; }\n" +
                "        .content { padding:16px 22px 12px; color:#333; }\n" +
                "        .alert { font-size:14px; font-weight:600; color:#1a1a1a; margin:0 0 10px; }\n" +
                "        .info { background:#f8f5f2; border-radius:4px; padding:8px 14px; margin:8px 0 12px; border:1px solid #f0ece8; }\n" +
                "        .info-item { display:flex; padding:3px 0; font-size:12px; border-bottom:1px solid #f0ece8; }\n" +
                "        .info-item:last-child { border-bottom:none; }\n" +
                "        .info-label { font-weight:600; color:#1a1a1a; min-width:70px; font-size:11px; flex-shrink:0; }\n" +
                "        .info-value { color:#555; word-break:break-word; font-size:12px; }\n" +
                "        .data-title { font-size:12px; font-weight:600; color:#1a1a1a; margin:0 0 4px; }\n" +
                "        .data-box { background:#faf8f6; padding:10px 14px; border-radius:4px; border-left:3px solid #FF6B35; font-family:'Courier New',monospace; font-size:12px; line-height:1.7; white-space:pre-wrap; word-wrap:break-word; color:#333; border:1px solid #f0ece8; border-left-width:3px; max-height:300px; overflow-y:auto; }\n" +
                "        .btn { display:inline-block; background:#FF6B35; color:#fff; padding:6px 20px; text-decoration:none; border-radius:4px; font-size:12px; font-weight:500; margin:6px 0 2px; }\n" +
                "        .btn:hover { background:#e85d2c; }\n" +
                "        .divider { border:none; height:1px; background:#eee; margin:10px 0; }\n" +
                "        .footer { background:#faf8f6; padding:10px 22px; text-align:center; border-top:1px solid #f0ece8; }\n" +
                "        .footer .brand { font-size:13px; font-weight:600; color:#222; margin:0 0 2px; }\n" +
                "        .footer .brand span { color:#FF6B35; }\n" +
                "        .footer p { margin:1px 0; color:#aaa; font-size:10px; }\n" +
                "        @media (max-width:480px) { .wrapper { padding:12px 8px; } .header { padding:12px 16px 10px; } .header h1 { font-size:16px; } .header img { max-height:26px; } .content { padding:12px 16px 10px; } .info-item { flex-direction:column; padding:4px 0; } .info-label { min-width:auto; font-size:10px; } .info-value { font-size:12px; } .data-box { font-size:11px; padding:8px 12px; max-height:200px; } .footer { padding:8px 16px; } }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"wrapper\">\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "           <img src=\"{{LOGO_SRC}}\" alt=\"Kunash Logo\">\n" +
                "            <h1>New Form Submission</h1>\n" +
                "            <span class=\"badge\">" + escapeHtml(formType) + "</span>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p class=\"alert\">" + iconBell() + " You have received a new submission</p>\n" +
                "            <div class=\"info\">\n" +
                "                <div class=\"info-item\">\n" +
                "                    <span class=\"info-label\">" + iconMailSmall() + " From</span>\n" +
                "                    <span class=\"info-value\">" + escapeHtml(userEmail) + "</span>\n" +
                "                </div>\n" +
                "                <div class=\"info-item\">\n" +
                "                    <span class=\"info-label\">" + iconClock() + " Time</span>\n" +
                "                    <span class=\"info-value\">" + timestamp + "</span>\n" +
                "                </div>\n" +
                "                <div class=\"info-item\">\n" +
                "                    <span class=\"info-label\">" + iconTag() + " Type</span>\n" +
                "                    <span class=\"info-value\">" + escapeHtml(formType) + "</span>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            <p class=\"data-title\">" + iconData() + " Form Data</p>\n" +
                "            <div class=\"data-box\">" + escapeHtml(formData) + "</div>\n" +
                "            <div style=\"text-align:center; margin:4px 0 2px;\">\n" +
                "                <a href=\"http://localhost:3000/admin\" class=\"btn\">Go to Admin Panel</a>\n" +
                "            </div>\n" +
                "            <hr class=\"divider\">\n" +
                "            <p style=\"color:#999; font-size:11px; text-align:center; font-style:italic; margin:2px 0;\">Log in to manage this submission</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p class=\"brand\">Kunash<span>.</span></p>\n" +
                "            <p>Automated notification</p>\n" +
                "            <p>&copy; " + currentYear + " Kunash. All rights reserved.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
    }

    // ==========================================
    // SVG ICON HELPERS
    // ==========================================

    private String iconNext() {
        return "<svg style=\"display:inline-block;vertical-align:middle;margin-right:4px;\" width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#FF6B35\" stroke-width=\"2.5\" stroke-linecap=\"round\" stroke-linejoin=\"round\">" +
                "<circle cx=\"12\" cy=\"12\" r=\"10\"/><path d=\"M9 12l2 2 4-4\"/></svg>";
    }

    private String iconMail() {
        return "<svg style=\"display:inline-block;vertical-align:middle;margin-right:4px;\" width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#FF6B35\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\">" +
                "<path d=\"M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z\"/><polyline points=\"22,6 12,13 2,6\"/></svg>";
    }

    private String iconBell() {
        return "<svg style=\"display:inline-block;vertical-align:middle;margin-right:4px;\" width=\"16\" height=\"16\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#FF6B35\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\">" +
                "<path d=\"M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9\"/><path d=\"M13.73 21a2 2 0 0 1-3.46 0\"/></svg>";
    }

    private String iconMailSmall() {
        return "<svg style=\"display:inline-block;vertical-align:middle;margin-right:3px;\" width=\"12\" height=\"12\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#888\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\">" +
                "<path d=\"M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z\"/><polyline points=\"22,6 12,13 2,6\"/></svg>";
    }

    private String iconClock() {
        return "<svg style=\"display:inline-block;vertical-align:middle;margin-right:3px;\" width=\"12\" height=\"12\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#888\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\">" +
                "<circle cx=\"12\" cy=\"12\" r=\"10\"/><polyline points=\"12 6 12 12 16 14\"/></svg>";
    }

    private String iconTag() {
        return "<svg style=\"display:inline-block;vertical-align:middle;margin-right:3px;\" width=\"12\" height=\"12\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#888\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\">" +
                "<path d=\"M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z\"/><line x1=\"7\" y1=\"7\" x2=\"7.01\" y2=\"7\"/></svg>";
    }

    private String iconData() {
        return "<svg style=\"display:inline-block;vertical-align:middle;margin-right:4px;\" width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#1a1a1a\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\">" +
                "<path d=\"M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z\"/><polyline points=\"14 2 14 8 20 8\"/><line x1=\"16\" y1=\"13\" x2=\"8\" y2=\"13\"/><line x1=\"16\" y1=\"17\" x2=\"8\" y2=\"17\"/></svg>";
    }

    // ==========================================
    // HELPER: Escape HTML
    // ==========================================

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}