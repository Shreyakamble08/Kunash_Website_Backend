package com.kunash.backend.repository;

import com.kunash.backend.entity.Application;
import com.kunash.backend.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    /**
     * Find all applications for a specific job
     */
    List<Application> findByJob(Job job);

    /**
     * Find all applications for a specific job, ordered by date (newest first)
     */
    List<Application> findByJobOrderByAppliedAtDesc(Job job);

    /**
     * Find all applications with a specific status (new, shortlisted, selected, rejected)
     */
    List<Application> findByStatus(String status);

    /**
     * Find all applications ordered by applied date (newest first)
     */
    List<Application> findAllByOrderByAppliedAtDesc();

    /**
     * Count how many applications a specific job has
     */
    long countByJob(Job job);

    /**
     * Count applications by job title (position) - For statistics
     */
    @Query("SELECT a.job.title, COUNT(a) FROM Application a GROUP BY a.job.title")
    List<Object[]> countApplicationsByJobTitle();
}