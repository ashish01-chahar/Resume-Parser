package com.parser.resume.service;

import com.parser.resume.dto.DashboardStatsDTO;
import com.parser.resume.model.Candidate;
import com.parser.resume.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    public Candidate saveCandidate(Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public Optional<Candidate> getCandidateById(Long id) {
        return candidateRepository.findById(id);
    }

    public void deleteCandidate(Long id) {
        candidateRepository.deleteById(id);
    }

    public DashboardStatsDTO getDashboardStats(String dbUrl) {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        long total = candidateRepository.count();
        stats.setTotalResumes(total);
        stats.setTotalCandidates(total);
        
        // Count uploads from today
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        long todayUploads = candidateRepository.findAll().stream()
                .filter(c -> c.getCreatedAt() != null && c.getCreatedAt().isAfter(startOfDay))
                .count();
        stats.setTodayUploads(todayUploads);
        
        // Mock average experience calculation for simplicity, can be improved
        stats.setAverageExperience(4.5);
        stats.setUniqueSkills(candidateRepository.findAll().stream()
                .flatMap(c -> c.getSkills().stream())
                .map(s -> s.getName().toLowerCase())
                .distinct()
                .count());

        stats.setDatabaseStatus(dbUrl != null && dbUrl.contains("h2") ? "H2 In-Memory" : "MySQL 3306");
        stats.setGeminiStatus((geminiApiKey != null && !geminiApiKey.trim().isEmpty()) ? "Connected" : "API Key Missing");
        stats.setAppVersion("1.0.0-Enterprise");

        return stats;
    }
}
