package com.example.JobTracker.repository;

import lombok.RequiredArgsConstructor;
import com.example.JobTracker.entity.ScraperConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

@Repository
public interface ScraperConfigRepository extends JpaRepository<ScraperConfig,Long> {
   @Query("Select s FROM ScraperConfig s WHERE s.domain_name=:domain_name")
   Optional<ScraperConfig> findByDomainName(@Param("domain_name") String domain_name);
}
