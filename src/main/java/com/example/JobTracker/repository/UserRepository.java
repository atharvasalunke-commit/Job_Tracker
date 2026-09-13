package com.example.JobTracker.repository;

import lombok.RequiredArgsConstructor;
import com.example.JobTracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    public Optional<User>findByUsername(String username);

    Optional<User> findByEmail(String email);
}
