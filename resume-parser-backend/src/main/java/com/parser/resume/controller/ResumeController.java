package com.parser.resume.controller;

import com.parser.resume.model.Resume;
import com.parser.resume.repository.ResumeRepository;
import com.parser.resume.service.ResumeParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin(origins = "*")
public class ResumeController {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ResumeParserService resumeParserService;

    // 1. Upload & Parse (No save)
    @PostMapping("/upload")
    public ResponseEntity<?> uploadAndParse(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty. Please upload a valid resume.");
        }
        try {
            Resume parsedResume = resumeParserService.parseResume(file);
            return ResponseEntity.ok(parsedResume);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error parsing resume: " + e.getMessage());
        }
    }

    // 2. Save Final Resume
    @PostMapping
    public ResponseEntity<Resume> saveResume(@RequestBody Resume resume) {
        // Correct bidirectional relationships
        if (resume.getSkills() != null) {
            resume.getSkills().forEach(s -> s.setResume(resume));
        }
        if (resume.getEducation() != null) {
            resume.getEducation().forEach(e -> e.setResume(resume));
        }
        if (resume.getExperience() != null) {
            resume.getExperience().forEach(exp -> exp.setResume(resume));
        }
        Resume saved = resumeRepository.save(resume);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 3. Get All Resumes
    @GetMapping
    public ResponseEntity<List<Resume>> getAllResumes() {
        List<Resume> resumes = resumeRepository.findAll();
        return ResponseEntity.ok(resumes);
    }

    // 4. Get Resume by ID
    @GetMapping("/{id}")
    public ResponseEntity<Resume> getResumeById(@PathVariable Long id) {
        Optional<Resume> resumeOpt = resumeRepository.findById(id);
        return resumeOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 5. Delete Resume
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResume(@PathVariable Long id) {
        if (!resumeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        resumeRepository.deleteById(id);
        return ResponseEntity.ok().body("Resume deleted successfully.");
    }
}
