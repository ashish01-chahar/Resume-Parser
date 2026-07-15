package com.parser.resume.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "submission_history")
public class SubmissionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resumeName;
    private String candidateName;
    private String uploadedBy;
    private LocalDateTime uploadDateTime;
    private String status;
    private Long fileSize;
    private String fileType;
    private String originalFileName;
    private String uploadedByRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @PrePersist
    protected void onCreate() {
        if (uploadDateTime == null) {
            uploadDateTime = LocalDateTime.now();
        }
    }

    public SubmissionHistory() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getResumeName() { return resumeName; }
    public void setResumeName(String resumeName) { this.resumeName = resumeName; }
    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
    public LocalDateTime getUploadDateTime() { return uploadDateTime; }
    public void setUploadDateTime(LocalDateTime uploadDateTime) { this.uploadDateTime = uploadDateTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    public String getUploadedByRole() { return uploadedByRole; }
    public void setUploadedByRole(String uploadedByRole) { this.uploadedByRole = uploadedByRole; }
    public Candidate getCandidate() { return candidate; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }
}
