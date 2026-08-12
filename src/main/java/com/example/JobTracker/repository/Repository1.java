package com.example.JobTracker.repository;

import com.example.JobTracker.entity.users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Repository1 extends JpaRepository<users,Long> {
}
