package com.parser.resume.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "experience")
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;
    private String role;
    private String duration;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    @JsonIgnore
    private Resume resume;

    public Experience() {}

    public Experience(Long id, String company, String role, String duration, String description, Resume resume) {
        this.id = id;
        this.company = company;
        this.role = role;
        this.duration = duration;
        this.description = description;
        this.resume = resume;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Resume getResume() { return resume; }
    public void setResume(Resume resume) { this.resume = resume; }

    @Override
    public String toString() {
        return "Experience{id=" + id + ", company='" + company + "', role='" + role + "'}";
    }
}
