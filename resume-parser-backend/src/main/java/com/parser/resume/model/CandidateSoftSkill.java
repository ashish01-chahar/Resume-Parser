package com.parser.resume.model;

import jakarta.persistence.*;

@Entity
@Table(name = "candidate_soft_skills")
public class CandidateSoftSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    public CandidateSoftSkill() {}

    public CandidateSoftSkill(String name, Candidate candidate) {
        this.name = name;
        this.candidate = candidate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Candidate getCandidate() { return candidate; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }
}
