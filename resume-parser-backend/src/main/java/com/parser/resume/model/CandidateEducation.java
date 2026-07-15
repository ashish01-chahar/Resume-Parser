package com.parser.resume.model;

import jakarta.persistence.*;

@Entity
@Table(name = "candidate_education")
public class CandidateEducation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String degree;
    private String institution;
    private String duration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    public CandidateEducation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }
    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public Candidate getCandidate() { return candidate; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }
}
