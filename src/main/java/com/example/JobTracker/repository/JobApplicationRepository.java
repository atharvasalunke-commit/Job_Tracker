package com.example.JobTracker.repository;

import lombok.RequiredArgsConstructor;
import com.example.JobTracker.entity.JobApplication;
import com.example.JobTracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication,Long> {
   @Query("SELECT j from JobApplication j where j.is_deleted=0")
   Page<JobApplication> findAllActive(Pageable pageable);
   @Query("SELECT j from JobApplication j where j.user =:user AND j.is_deleted=0")
   Page<JobApplication>findAllActiveByUser(@Param("user") User user,Pageable pageable);
   Optional<JobApplication>findByIdAndUser(Long id,User user);
   @Query("SELECT COUNT(j) > 0 FROM JobApplication j WHERE j.code = :code AND j.user = :user AND j.is_deleted = 0")
   boolean existsByCodeAndUser(@Param("code") String code, @Param("user") User user);
}
