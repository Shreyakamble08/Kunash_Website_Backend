package com.kunash.backend.repository;

import com.kunash.backend.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    /**
     * Find all jobs with a specific status (active or closed)
     * This method name follows Spring Data JPA naming convention
     * It will automatically generate the SQL:
     * SELECT * FROM jobs WHERE status = ?
     */
    List<Job> findByStatus(String status);

    /**
     * Find all jobs ordered by creation date (newest first)
     * This will generate: SELECT * FROM jobs ORDER BY created_at DESC
     */
    List<Job> findAllByOrderByCreatedAtDesc();
}