package com.parser.resume.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "education")
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String degree;
    private String institution;
    private String duration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    @JsonIgnore
    private Resume resume;

    public Education() {}

    public Education(Long id, String degree, String institution, String duration, Resume resume) {
        this.id = id;
        this.degree = degree;
        this.institution = institution;
        this.duration = duration;
        this.resume = resume;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }
    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public Resume getResume() { return resume; }
    public void setResume(Resume resume) { this.resume = resume; }

    @Override
    public String toString() {
        return "Education{id=" + id + ", degree='" + degree + "', institution='" + institution + "'}";
    }
}
