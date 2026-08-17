package com.kunash.backend.service.impl;

import com.kunash.backend.dto.request.ContactRequest;
import com.kunash.backend.dto.response.ContactResponse;
import com.kunash.backend.entity.Contact;
import com.kunash.backend.repository.ContactRepository;
import com.kunash.backend.service.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    @Override
    @Transactional
    public ContactResponse submitContact(ContactRequest request) {
        log.info("Submitting contact from: {}", request.getEmail());

        // Step 1: Convert Request DTO to Entity
        Contact contact = new Contact();
        contact.setName(request.getName());
        contact.setPhone(request.getPhone());
        contact.setEmail(request.getEmail());
        contact.setSubject(request.getSubject());
        contact.setMessage(request.getMessage());
        contact.setCreatedAt(LocalDateTime.now());

        // Step 2: Save to Database
        Contact savedContact = contactRepository.save(contact);
        log.info("Contact saved with ID: {}", savedContact.getId());

        // Step 3: Convert Entity to Response DTO
        return convertToResponse(savedContact);
    }

    @Override
    public List<ContactResponse> getAllContacts() {
        log.info("Fetching all contacts");

        // Step 1: Get all contacts from database (ordered by date desc)
        List<Contact> contacts = contactRepository.findAllByOrderByCreatedAtDesc();

        // Step 2: Convert each Entity to Response DTO
        return contacts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ContactResponse getContactById(Long id) {
        log.info("Fetching contact with ID: {}", id);

        // Step 1: Find contact by ID or throw exception
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found with id: " + id));

        // Step 2: Convert Entity to Response DTO
        return convertToResponse(contact);
    }

    @Override
    @Transactional
    public void deleteContact(Long id) {
        log.info("Deleting contact with ID: {}", id);

        // Step 1: Check if contact exists
        if (!contactRepository.existsById(id)) {
            throw new RuntimeException("Contact not found with id: " + id);
        }

        // Step 2: Delete the contact
        contactRepository.deleteById(id);
        log.info("Contact deleted with ID: {}", id);
    }

    // ============ HELPER METHOD ============

    /**
     * Convert Contact Entity to ContactResponse DTO
     */
    private ContactResponse convertToResponse(Contact contact) {
        return new ContactResponse(
                contact.getId(),
                contact.getName(),
                contact.getPhone(),
                contact.getEmail(),
                contact.getSubject(),
                contact.getMessage(),
                contact.getCreatedAt()
        );
    }
}