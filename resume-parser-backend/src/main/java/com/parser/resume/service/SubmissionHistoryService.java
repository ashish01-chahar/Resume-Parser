package com.parser.resume.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.parser.resume.dto.SubmissionHistoryDTO;
import com.parser.resume.model.Candidate;
import com.parser.resume.model.SubmissionHistory;
import com.parser.resume.repository.SubmissionHistoryRepository;

@Service
public class SubmissionHistoryService {

    @Autowired
    private SubmissionHistoryRepository submissionHistoryRepository;

    @Transactional
    public SubmissionHistoryDTO createEntry(Candidate candidate, String uploadedBy, String uploadedByRole, String originalFileName, long fileSize, String fileType) {
        SubmissionHistory history = new SubmissionHistory();
        history.setResumeName(candidate.getResumeFileName() != null ? candidate.getResumeFileName() : originalFileName);
        history.setCandidateName(candidate.getFullName());
        history.setUploadedBy(uploadedBy);
        history.setUploadedByRole(uploadedByRole);
        history.setStatus("Processed");
        history.setFileSize(fileSize);
        history.setFileType(fileType);
        history.setOriginalFileName(originalFileName);
        history.setCandidate(candidate);
        SubmissionHistory saved = submissionHistoryRepository.save(history);
        return toDTO(saved);
    }

    public List<SubmissionHistoryDTO> getAll() {
        return submissionHistoryRepository.findAllByOrderByUploadDateTimeDesc().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Optional<SubmissionHistoryDTO> getById(Long id) {
        return submissionHistoryRepository.findById(id).map(this::toDTO);
    }

    @Transactional
    public void deleteById(Long id) {
        submissionHistoryRepository.deleteById(id);
    }

    private SubmissionHistoryDTO toDTO(SubmissionHistory history) {
        SubmissionHistoryDTO dto = new SubmissionHistoryDTO();
        dto.setId(history.getId());
        dto.setResumeName(history.getResumeName());
        dto.setCandidateName(history.getCandidateName());
        dto.setUploadedBy(history.getUploadedBy());
        dto.setUploadDateTime(history.getUploadDateTime());
        dto.setStatus(history.getStatus());
        dto.setFileSize(history.getFileSize());
        dto.setFileType(history.getFileType());
        dto.setOriginalFileName(history.getOriginalFileName());
        dto.setUploadedByRole(history.getUploadedByRole());
        dto.setCandidateId(history.getCandidate() != null ? history.getCandidate().getId() : null);
        return dto;
    }
}
