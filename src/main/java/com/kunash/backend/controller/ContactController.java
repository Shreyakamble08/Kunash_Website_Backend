package com.kunash.backend.controller;

import com.kunash.backend.dto.request.ContactRequest;
import com.kunash.backend.dto.response.ApiResponse;
import com.kunash.backend.dto.response.ContactResponse;
import com.kunash.backend.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://localhost:5500",
        "http://127.0.0.1:5500",
        "http://localhost:5501",
        "http://127.0.0.1:5501"
})
@RequiredArgsConstructor
@Slf4j
public class ContactController {

    private final ContactService contactService;

    // =============================================
    //  PUBLIC ENDPOINT - No Authentication Required
    // =============================================

    /**
     * Submit a contact form
     * Anyone can submit - no authentication needed
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<ContactResponse>> submitContact(
            @Valid @RequestBody ContactRequest request) {

        log.info("📝 New contact form submission from: {}", request.getEmail());

        ContactResponse response = contactService.submitContact(request);

        return ResponseEntity.ok(
                ApiResponse.success("Thank you! We'll get back to you within 24 hours.", response)
        );
    }

    // =============================================
    //  ADMIN ENDPOINTS - Authentication Required
    // =============================================

    /**
     * Get all contacts (Admin only)
     * Requires valid JWT token
     */
    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<List<ContactResponse>>> getAllContacts() {
        log.info("📋 Fetching all contacts");

        List<ContactResponse> contacts = contactService.getAllContacts();

        return ResponseEntity.ok(
                ApiResponse.success("Contacts retrieved successfully", contacts)
        );
    }

    /**
     * Get a single contact by ID (Admin only)
     * Requires valid JWT token
     */
    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<ContactResponse>> getContactById(
            @PathVariable Long id) {

        log.info("🔍 Fetching contact with ID: {}", id);

        ContactResponse contact = contactService.getContactById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Contact retrieved successfully", contact)
        );
    }

    /**
     * Delete a contact by ID (Admin only)
     * Requires valid JWT token
     */
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContact(
            @PathVariable Long id) {

        log.info("🗑️ Deleting contact with ID: {}", id);

        contactService.deleteContact(id);

        return ResponseEntity.ok(
                ApiResponse.success("Contact deleted successfully")
        );
    }

    /**
     * Get count of all contacts (Admin only)
     * Requires valid JWT token
     */
    @GetMapping("/admin/count")
    public ResponseEntity<ApiResponse<Long>> getContactsCount() {
        log.info("📊 Fetching contacts count");

        List<ContactResponse> contacts = contactService.getAllContacts();
        long count = contacts.size();

        return ResponseEntity.ok(
                ApiResponse.success("Total contacts: " + count, count)
        );
    }
}