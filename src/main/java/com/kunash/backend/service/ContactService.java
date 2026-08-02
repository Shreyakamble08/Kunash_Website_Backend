package com.kunash.backend.service;

import com.kunash.backend.dto.request.ContactRequest;
import com.kunash.backend.dto.response.ContactResponse;

import java.util.List;

public interface ContactService {

    /**
     * Submit a new contact form
     * @param request The contact form data
     * @return The saved contact with ID and timestamp
     */
    ContactResponse submitContact(ContactRequest request);

    /**
     * Get all contacts ordered by creation date (newest first)
     * @return List of all contacts
     */
    List<ContactResponse> getAllContacts();

    /**
     * Get a specific contact by ID
     * @param id The contact ID
     * @return The contact details
     * @throws RuntimeException if contact not found
     */
    ContactResponse getContactById(Long id);

    /**
     * Delete a contact by ID
     * @param id The contact ID
     * @throws RuntimeException if contact not found
     */
    void deleteContact(Long id);
}