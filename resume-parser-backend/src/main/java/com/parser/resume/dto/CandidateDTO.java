package com.parser.resume.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CandidateDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String linkedin;
    private String github;
    private String location;
    private String summary;
    private String rawJson;
    private String resumeFileName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<SkillDTO> skills = new ArrayList<>();
    private List<SkillDTO> softSkills = new ArrayList<>();
    private List<SkillDTO> languages = new ArrayList<>();
    private List<SkillDTO> certifications = new ArrayList<>();
    private List<ProjectDTO> projects = new ArrayList<>();
    private List<EducationDTO> education = new ArrayList<>();
    private List<ExperienceDTO> experience = new ArrayList<>();

    public CandidateDTO() {}

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
    public String getResumeFileName() { return resumeFileName; }
    public void setResumeFileName(String resumeFileName) { this.resumeFileName = resumeFileName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<SkillDTO> getSkills() { return skills; }
    public void setSkills(List<SkillDTO> skills) { this.skills = skills; }
    public List<SkillDTO> getSoftSkills() { return softSkills; }
    public void setSoftSkills(List<SkillDTO> softSkills) { this.softSkills = softSkills; }
    public List<SkillDTO> getLanguages() { return languages; }
    public void setLanguages(List<SkillDTO> languages) { this.languages = languages; }
    public List<SkillDTO> getCertifications() { return certifications; }
    public void setCertifications(List<SkillDTO> certifications) { this.certifications = certifications; }
    public List<ProjectDTO> getProjects() { return projects; }
    public void setProjects(List<ProjectDTO> projects) { this.projects = projects; }
    public List<EducationDTO> getEducation() { return education; }
    public void setEducation(List<EducationDTO> education) { this.education = education; }
    public List<ExperienceDTO> getExperience() { return experience; }
    public void setExperience(List<ExperienceDTO> experience) { this.experience = experience; }

    // Nested DTOs
    public static class SkillDTO {
        private String name;
        public SkillDTO() {}
        public SkillDTO(String name) { this.name = name; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class ProjectDTO {
        private String title;
        private String description;
        private String technologies;
        public ProjectDTO() {}
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getTechnologies() { return technologies; }
        public void setTechnologies(String technologies) { this.technologies = technologies; }
    }

    public static class EducationDTO {
        private String degree;
        private String institution;
        private String duration;
        public EducationDTO() {}
        public String getDegree() { return degree; }
        public void setDegree(String degree) { this.degree = degree; }
        public String getInstitution() { return institution; }
        public void setInstitution(String institution) { this.institution = institution; }
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
    }

    public static class ExperienceDTO {
        private String company;
        private String role;
        private String duration;
        private String description;
        public ExperienceDTO() {}
        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
