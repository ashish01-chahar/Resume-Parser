package com.parser.resume.dto;

public class DashboardStatsDTO {
    private long totalResumes;
    private long uniqueSkills;
    private long todayUploads;
    private long totalCandidates;
    private double averageExperience;
    private String databaseStatus;
    private String geminiStatus;
    private String appVersion;

    public DashboardStatsDTO() {}

    public long getTotalResumes() { return totalResumes; }
    public void setTotalResumes(long totalResumes) { this.totalResumes = totalResumes; }
    public long getUniqueSkills() { return uniqueSkills; }
    public void setUniqueSkills(long uniqueSkills) { this.uniqueSkills = uniqueSkills; }
    public long getTodayUploads() { return todayUploads; }
    public void setTodayUploads(long todayUploads) { this.todayUploads = todayUploads; }
    public long getTotalCandidates() { return totalCandidates; }
    public void setTotalCandidates(long totalCandidates) { this.totalCandidates = totalCandidates; }
    public double getAverageExperience() { return averageExperience; }
    public void setAverageExperience(double averageExperience) { this.averageExperience = averageExperience; }
    public String getDatabaseStatus() { return databaseStatus; }
    public void setDatabaseStatus(String databaseStatus) { this.databaseStatus = databaseStatus; }
    public String getGeminiStatus() { return geminiStatus; }
    public void setGeminiStatus(String geminiStatus) { this.geminiStatus = geminiStatus; }
    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }
}
