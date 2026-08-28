package com.example.JobTracker.repository;

import com.example.JobTracker.entity.job_applications;
import com.example.JobTracker.entity.users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Repository2 extends JpaRepository<job_applications,Long> {
   @Query("SELECT j from job_applications j where j.is_deleted=0")
   Page<job_applications> findAllActive(Pageable pageable);
   @Query("SELECT j from job_applications j where j.user =:user AND j.is_deleted=0")
   Page<job_applications>findAllActiveByUser(@Param("user") users user,Pageable pageable);
   Optional<job_applications>findByIdAndUser(Long id,users user);
}
