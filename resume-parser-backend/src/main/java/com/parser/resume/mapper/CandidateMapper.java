package com.parser.resume.mapper;

import com.parser.resume.dto.CandidateDTO;
import com.parser.resume.model.*;

import java.util.stream.Collectors;

public class CandidateMapper {

    public static CandidateDTO toDTO(Candidate candidate) {
        if (candidate == null) return null;
        CandidateDTO dto = new CandidateDTO();
        dto.setId(candidate.getId());
        dto.setFullName(candidate.getFullName());
        dto.setEmail(candidate.getEmail());
        dto.setPhone(candidate.getPhone());
        dto.setLinkedin(candidate.getLinkedin());
        dto.setGithub(candidate.getGithub());
        dto.setLocation(candidate.getLocation());
        dto.setSummary(candidate.getSummary());
        dto.setRawJson(candidate.getRawJson());
        dto.setResumeFileName(candidate.getResumeFileName());
        dto.setCreatedAt(candidate.getCreatedAt());
        dto.setUpdatedAt(candidate.getUpdatedAt());

        if (candidate.getSkills() != null) {
            dto.setSkills(candidate.getSkills().stream()
                    .map(s -> new CandidateDTO.SkillDTO(s.getName()))
                    .collect(Collectors.toList()));
        }
        if (candidate.getSoftSkills() != null) {
            dto.setSoftSkills(candidate.getSoftSkills().stream()
                    .map(s -> new CandidateDTO.SkillDTO(s.getName()))
                    .collect(Collectors.toList()));
        }
        if (candidate.getLanguages() != null) {
            dto.setLanguages(candidate.getLanguages().stream()
                    .map(s -> new CandidateDTO.SkillDTO(s.getName()))
                    .collect(Collectors.toList()));
        }
        if (candidate.getCertifications() != null) {
            dto.setCertifications(candidate.getCertifications().stream()
                    .map(s -> new CandidateDTO.SkillDTO(s.getName()))
                    .collect(Collectors.toList()));
        }
        if (candidate.getProjects() != null) {
            dto.setProjects(candidate.getProjects().stream().map(p -> {
                CandidateDTO.ProjectDTO pDto = new CandidateDTO.ProjectDTO();
                pDto.setTitle(p.getTitle());
                pDto.setDescription(p.getDescription());
                pDto.setTechnologies(p.getTechnologies());
                return pDto;
            }).collect(Collectors.toList()));
        }
        if (candidate.getEducation() != null) {
            dto.setEducation(candidate.getEducation().stream().map(e -> {
                CandidateDTO.EducationDTO eDto = new CandidateDTO.EducationDTO();
                eDto.setDegree(e.getDegree());
                eDto.setInstitution(e.getInstitution());
                eDto.setDuration(e.getDuration());
                return eDto;
            }).collect(Collectors.toList()));
        }
        if (candidate.getExperience() != null) {
            dto.setExperience(candidate.getExperience().stream().map(e -> {
                CandidateDTO.ExperienceDTO eDto = new CandidateDTO.ExperienceDTO();
                eDto.setCompany(e.getCompany());
                eDto.setRole(e.getRole());
                eDto.setDuration(e.getDuration());
                eDto.setDescription(e.getDescription());
                return eDto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}
