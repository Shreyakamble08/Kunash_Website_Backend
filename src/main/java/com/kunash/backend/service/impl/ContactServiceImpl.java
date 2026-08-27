package com.kunash.backend.service.impl;

import com.kunash.backend.dto.request.ContactRequest;
import com.kunash.backend.dto.response.ContactResponse;
import com.kunash.backend.entity.Contact;
import com.kunash.backend.repository.ContactRepository;
import com.kunash.backend.service.ContactService;
import com.kunash.backend.service.EmailService;
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
    private final EmailService emailService;  // ← Add this

    @Override
    @Transactional
    public ContactResponse submitContact(ContactRequest request) {
        log.info("Submitting contact from: {}", request.getEmail());

        Contact contact = new Contact();
        contact.setName(request.getName());
        contact.setPhone(request.getPhone());
        contact.setEmail(request.getEmail());
        contact.setSubject(request.getSubject());
        contact.setMessage(request.getMessage());
        contact.setCreatedAt(LocalDateTime.now());

        Contact savedContact = contactRepository.save(contact);
        log.info("Contact saved with ID: {}", savedContact.getId());

        // ========== SEND EMAILS ==========
        try {
            emailService.sendThankYouEmail(
                    savedContact.getEmail(),
                    savedContact.getName(),
                    "Contact"
            );
        } catch (Exception e) {
            log.error("Failed to send thank you email: {}", e.getMessage());
        }

        try {
            String formData = String.format(
                    "Name: %s\nEmail: %s\nPhone: %s\nSubject: %s\nMessage: %s\nSubmitted: %s",
                    savedContact.getName(),
                    savedContact.getEmail(),
                    savedContact.getPhone(),
                    savedContact.getSubject(),
                    savedContact.getMessage(),
                    savedContact.getCreatedAt()
            );
            emailService.sendAdminNotification(formData, "Contact", savedContact.getEmail());
        } catch (Exception e) {
            log.error("Failed to send admin notification: {}", e.getMessage());
        }

        return convertToResponse(savedContact);
    }

    @Override
    public List<ContactResponse> getAllContacts() {
        List<Contact> contacts = contactRepository.findAllByOrderByCreatedAtDesc();
        return contacts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ContactResponse getContactById(Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found with id: " + id));
        return convertToResponse(contact);
    }

    @Override
    @Transactional
    public void deleteContact(Long id) {
        if (!contactRepository.existsById(id)) {
            throw new RuntimeException("Contact not found with id: " + id);
        }
        contactRepository.deleteById(id);
        log.info("Contact deleted with ID: {}", id);
    }

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