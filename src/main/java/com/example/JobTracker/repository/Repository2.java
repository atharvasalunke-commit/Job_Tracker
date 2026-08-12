package com.example.JobTracker.repository;

import com.example.JobTracker.entity.job_applications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Repository2 extends JpaRepository<job_applications,Long> {
   @Query("SELECT j from job_applications j where j.is_deleted=0")
    List<job_applications> findAllActive();
}
