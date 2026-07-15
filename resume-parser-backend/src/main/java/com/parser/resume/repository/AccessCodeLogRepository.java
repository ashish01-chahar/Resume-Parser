package com.parser.resume.repository;

import com.parser.resume.model.AccessCodeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessCodeLogRepository extends JpaRepository<AccessCodeLog, Long> {
}
