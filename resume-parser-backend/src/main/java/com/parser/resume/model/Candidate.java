package com.parser.resume.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "candidates")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String phone;
    private String linkedin;
    private String github;
    private String location;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String summary;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String rawJson;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] resumeFile;
    private String resumeFileName;
    private String resumeFileContentType;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidateSkill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidateSoftSkill> softSkills = new ArrayList<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidateLanguage> languages = new ArrayList<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidateCertification> certifications = new ArrayList<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidateProject> projects = new ArrayList<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidateEducation> education = new ArrayList<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidateExperience> experience = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Candidate() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getLinkedin() { return linkedin; }
    public void setLinkedin(String linkedin) { this.linkedin = linkedin; }
    public String getGithub() { return github; }
    public void setGithub(String github) { this.github = github; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getRawJson() { return rawJson; }
    public void setRawJson(String rawJson) { this.rawJson = rawJson; }
    public byte[] getResumeFile() { return resumeFile; }
    public void setResumeFile(byte[] resumeFile) { this.resumeFile = resumeFile; }
    public String getResumeFileName() { return resumeFileName; }
    public void setResumeFileName(String resumeFileName) { this.resumeFileName = resumeFileName; }
    public String getResumeFileContentType() { return resumeFileContentType; }
    public void setResumeFileContentType(String resumeFileContentType) { this.resumeFileContentType = resumeFileContentType; }
    
    public List<CandidateSkill> getSkills() { return skills; }
    public void setSkills(List<CandidateSkill> skills) { this.skills = skills; }
    public List<CandidateSoftSkill> getSoftSkills() { return softSkills; }
    public void setSoftSkills(List<CandidateSoftSkill> softSkills) { this.softSkills = softSkills; }
    public List<CandidateLanguage> getLanguages() { return languages; }
    public void setLanguages(List<CandidateLanguage> languages) { this.languages = languages; }
    public List<CandidateCertification> getCertifications() { return certifications; }
    public void setCertifications(List<CandidateCertification> certifications) { this.certifications = certifications; }
    public List<CandidateProject> getProjects() { return projects; }
    public void setProjects(List<CandidateProject> projects) { this.projects = projects; }
    public List<CandidateEducation> getEducation() { return education; }
    public void setEducation(List<CandidateEducation> education) { this.education = education; }
    public List<CandidateExperience> getExperience() { return experience; }
    public void setExperience(List<CandidateExperience> experience) { this.experience = experience; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
