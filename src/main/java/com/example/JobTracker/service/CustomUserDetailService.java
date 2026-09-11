package com.example.JobTracker.service;

import com.example.JobTracker.entity.User;

import com.example.JobTracker.globalexception.ResourceNotFound;
import com.example.JobTracker.repository.Repository1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private Repository1 repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=repo.findByUsername(username).orElseThrow(()->new ResourceNotFound("User not found with this username"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),user.getPassword_Hash(), List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
