package com.example.JobTracker.entity;

import lombok.Getter;
import lombok.Setter;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name="job_applications")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class JobApplication {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    private String company_name;
    private String job_title;
    private String status;
    private String application_url;
    @Column(nullable=false,updatable=false)
    private String created_at;
    @Column(nullable=false)
    private String updated_at;
    private int is_deleted;
    private String code;
    private String job_description;

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    

    public void setCreated_at() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.created_at= LocalDateTime.now().format(formatter);
    }

    

    public void setUpdated_at() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.updated_at= LocalDateTime.now().format(formatter);
    }

    

    

    

    
}
