package com.example.JobTracker.entity;

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
public class job_applications {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private users user;
    @NotNull
    private String company_name;
    @NotNull
    private  String job_title;
    @NotNull
    private String  status;
    private String application_url;
    @CreatedDate
    @Column(nullable=false,updatable=false)
    private String created_at;
    @LastModifiedDate
    @Column(nullable=false)
    private String updated_at;
    private int is_deleted;
    public Long getId() {
        return id;
    }


    public users getUser() {
        return user;
    }

    public void setUser(users user) {
        this.user = user;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplication_url() {
        return application_url;
    }

    public void setApplication_url(String application_url) {
        this.application_url = application_url;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.created_at= LocalDateTime.now().format(formatter);
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.updated_at= LocalDateTime.now().format(formatter);
    }

    public int getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(int is_deleted) {
        this.is_deleted = is_deleted;
    }
}