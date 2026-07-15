package com.parser.resume.controller;

import com.parser.resume.dto.AnalyticsDTO;
import com.parser.resume.dto.DashboardStatsDTO;
import com.parser.resume.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private CandidateService candidateService;

    @Value("${spring.datasource.url:}")
    private String dbUrl;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        DashboardStatsDTO stats = candidateService.getDashboardStats(dbUrl);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/skills")
    public ResponseEntity<List<String>> getAllSkills() {
        // Simplified mapping for all unique technical skills
        List<String> skills = candidateService.getAllCandidates().stream()
                .flatMap(c -> c.getSkills().stream())
                .map(s -> s.getName())
                .distinct()
                .sorted()
                .toList();
        return ResponseEntity.ok(skills);
    }

    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsDTO> getAnalytics() {
        AnalyticsDTO analytics = new AnalyticsDTO();
        
        // Mocked or calculated data for Recharts
        // Top Technologies
        analytics.setTopTechnologies(List.of(
                java.util.Map.of("name", "React", "value", 45),
                java.util.Map.of("name", "Java", "value", 38),
                java.util.Map.of("name", "Spring Boot", "value", 30),
                java.util.Map.of("name", "MySQL", "value", 25)
        ));
        
        // Experience Distribution
        analytics.setExperienceDistribution(List.of(
                java.util.Map.of("name", "0-2 Years", "value", 15),
                java.util.Map.of("name", "3-5 Years", "value", 30),
                java.util.Map.of("name", "5-10 Years", "value", 20),
                java.util.Map.of("name", "10+ Years", "value", 5)
        ));
        
        // Education Levels
        analytics.setEducationLevels(List.of(
                java.util.Map.of("name", "Bachelor's", "value", 60),
                java.util.Map.of("name", "Master's", "value", 25),
                java.util.Map.of("name", "Ph.D.", "value", 5),
                java.util.Map.of("name", "Other", "value", 10)
        ));
        
        // Monthly Uploads
        analytics.setMonthlyUploads(List.of(
                java.util.Map.of("name", "Jan", "uploads", 12),
                java.util.Map.of("name", "Feb", "uploads", 19),
                java.util.Map.of("name", "Mar", "uploads", 15),
                java.util.Map.of("name", "Apr", "uploads", 22)
        ));

        return ResponseEntity.ok(analytics);
    }
}
