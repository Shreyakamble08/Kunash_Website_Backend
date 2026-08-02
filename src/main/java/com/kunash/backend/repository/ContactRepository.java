package com.kunash.backend.repository;

import com.kunash.backend.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    /**
     * Get all contacts ordered by creation date (newest first)
     * This will be used in the admin panel
     */
    List<Contact> findAllByOrderByCreatedAtDesc();
}