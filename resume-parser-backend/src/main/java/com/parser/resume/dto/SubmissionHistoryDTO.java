package com.parser.resume.dto;

import java.time.LocalDateTime;

public class SubmissionHistoryDTO {
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
    private Long candidateId;

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
    public Long getCandidateId() { return candidateId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }
}
