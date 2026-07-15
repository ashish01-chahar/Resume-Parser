package com.parser.resume.dto;

public class AuthResponseDTO {
    private String token;
    private String role;
    private String email;
    private String fullName;

    public AuthResponseDTO() {}

    public AuthResponseDTO(String token, String role, String email, String fullName) {
        this.token = token;
        this.role = role;
        this.email = email;
        this.fullName = fullName;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
