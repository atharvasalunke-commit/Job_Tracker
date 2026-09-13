package com.example.JobTracker.service;

import lombok.RequiredArgsConstructor;
import com.example.JobTracker.entity.User;
import com.example.JobTracker.exception.ResourceNotFound;
import com.example.JobTracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor

public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=repo.findByUsername(username).orElseThrow(()->new ResourceNotFound("User not found with this username"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword_Hash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
