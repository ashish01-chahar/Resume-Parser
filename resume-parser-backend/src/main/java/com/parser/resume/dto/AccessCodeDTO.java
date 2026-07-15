package com.parser.resume.dto;

import java.time.LocalDateTime;

public class AccessCodeDTO {
    private Long id;
    private String code;
    private String assignedName;
    private String assignedEmail;
    private LocalDateTime expiryDate;
    private Integer maxUses;
    private Integer usedCount;
    private boolean active;
    private boolean disabled;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getAssignedName() { return assignedName; }
    public void setAssignedName(String assignedName) { this.assignedName = assignedName; }
    public String getAssignedEmail() { return assignedEmail; }
    public void setAssignedEmail(String assignedEmail) { this.assignedEmail = assignedEmail; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    public Integer getMaxUses() { return maxUses; }
    public void setMaxUses(Integer maxUses) { this.maxUses = maxUses; }
    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isDisabled() { return disabled; }
    public void setDisabled(boolean disabled) { this.disabled = disabled; }
}
