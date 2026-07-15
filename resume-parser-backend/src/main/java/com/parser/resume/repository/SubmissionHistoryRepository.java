package com.parser.resume.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parser.resume.model.SubmissionHistory;

public interface SubmissionHistoryRepository extends JpaRepository<SubmissionHistory, Long> {
    List<SubmissionHistory> findAllByOrderByUploadDateTimeDesc();
}
