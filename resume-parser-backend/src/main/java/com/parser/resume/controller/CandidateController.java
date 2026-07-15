package com.parser.resume.controller;

import com.parser.resume.dto.CandidateDTO;
import com.parser.resume.mapper.CandidateMapper;
import com.parser.resume.model.Candidate;
import com.parser.resume.service.CandidateService;
import com.parser.resume.service.GeminiParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private GeminiParserService geminiParserService;

    @PostMapping("/resumes/upload")
    public ResponseEntity<?> uploadAndParse(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty. Please upload a valid resume.");
        }
        try {
            Candidate parsedCandidate = geminiParserService.parseResume(file);
            Candidate savedCandidate = candidateService.saveCandidate(parsedCandidate);
            CandidateDTO dto = CandidateMapper.toDTO(savedCandidate);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error parsing resume: " + e.getMessage());
        }
    }

    @GetMapping("/candidates")
    public ResponseEntity<List<CandidateDTO>> getAllCandidates() {
        List<Candidate> candidates = candidateService.getAllCandidates();
        List<CandidateDTO> dtos = candidates.stream()
                .map(CandidateMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/candidates/{id}")
    public ResponseEntity<CandidateDTO> getCandidateById(@PathVariable Long id) {
        Optional<Candidate> opt = candidateService.getCandidateById(id);
        return opt.map(CandidateMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/candidates/{id}")
    public ResponseEntity<?> deleteCandidate(@PathVariable Long id) {
        Optional<Candidate> opt = candidateService.getCandidateById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        candidateService.deleteCandidate(id);
        return ResponseEntity.ok().body("Candidate deleted successfully.");
    }

    @GetMapping("/candidates/{id}/download")
    public ResponseEntity<byte[]> downloadResume(@PathVariable Long id) {
        Optional<Candidate> opt = candidateService.getCandidateById(id);
        if (opt.isEmpty() || opt.get().getResumeFile() == null) {
            return ResponseEntity.notFound().build();
        }
        
        Candidate candidate = opt.get();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + candidate.getResumeFileName() + "\"")
                .contentType(MediaType.parseMediaType(candidate.getResumeFileContentType() != null ? candidate.getResumeFileContentType() : "application/octet-stream"))
                .body(candidate.getResumeFile());
    }
}
