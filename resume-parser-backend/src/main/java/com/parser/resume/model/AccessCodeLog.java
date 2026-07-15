package com.parser.resume.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_code_logs")
public class AccessCodeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "access_code_id")
    private AccessCode accessCode;

    private String email;
    private String status;
    private LocalDateTime usedAt;

    public AccessCodeLog() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public AccessCode getAccessCode() { return accessCode; }
    public void setAccessCode(AccessCode accessCode) { this.accessCode = accessCode; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getUsedAt() { return usedAt; }
    public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }
}
