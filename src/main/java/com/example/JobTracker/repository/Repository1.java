package com.example.JobTracker.repository;

import com.example.JobTracker.entity.users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Repository1 extends JpaRepository<users,Long> {

    public Optional<users>findByUsername(String username);
}
