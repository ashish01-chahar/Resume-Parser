package com.parser.resume.dto;

import java.util.List;
import java.util.Map;

public class AnalyticsDTO {
    private List<Map<String, Object>> topTechnologies;
    private List<Map<String, Object>> experienceDistribution;
    private List<Map<String, Object>> educationLevels;
    private List<Map<String, Object>> monthlyUploads;

    public AnalyticsDTO() {}

    public List<Map<String, Object>> getTopTechnologies() { return topTechnologies; }
    public void setTopTechnologies(List<Map<String, Object>> topTechnologies) { this.topTechnologies = topTechnologies; }
    public List<Map<String, Object>> getExperienceDistribution() { return experienceDistribution; }
    public void setExperienceDistribution(List<Map<String, Object>> experienceDistribution) { this.experienceDistribution = experienceDistribution; }
    public List<Map<String, Object>> getEducationLevels() { return educationLevels; }
    public void setEducationLevels(List<Map<String, Object>> educationLevels) { this.educationLevels = educationLevels; }
    public List<Map<String, Object>> getMonthlyUploads() { return monthlyUploads; }
    public void setMonthlyUploads(List<Map<String, Object>> monthlyUploads) { this.monthlyUploads = monthlyUploads; }
}
