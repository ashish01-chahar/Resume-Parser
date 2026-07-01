package com.parser.resume.service;

import com.parser.resume.model.Education;
import com.parser.resume.model.Experience;
import com.parser.resume.model.Resume;
import com.parser.resume.model.Skill;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumeParserService {

    private final Tika tika = new Tika();

    // List of common skills to match
    private static final List<String> COMMON_SKILLS = Arrays.asList(
        "Java", "Spring Boot", "Spring", "Hibernate", "JPA", "SQL", "MySQL", "PostgreSQL", "Oracle",
        "MongoDB", "Redis", "Cassandra", "React", "Angular", "Vue", "JavaScript", "TypeScript",
        "HTML", "CSS", "Tailwind", "Node.js", "Express", "Python", "Django", "Flask", "C++", "C#",
        "Ruby", "Rails", "Go", "Golang", "Rust", "Swift", "Kotlin", "Docker", "Kubernetes", "AWS",
        "Azure", "GCP", "Git", "GitHub", "CI/CD", "Jenkins", "JUnit", "Mockito", "Selenium", "REST API",
        "GraphQL", "Microservices", "Kafka", "RabbitMQ", "Machine Learning", "Data Science", "Linux"
    );

    public Resume parseResume(MultipartFile file) throws Exception {
        String text;
        try (InputStream stream = file.getInputStream()) {
            text = tika.parseToString(stream);
        }

        Resume resume = new Resume();
        resume.setRawText(text);

        // 1. Extract Email
        String email = extractEmail(text);
        resume.setEmail(email);

        // 2. Extract Phone Number
        String phone = extractPhone(text);
        resume.setPhone(phone);

        // 3. Extract Name
        String name = extractName(text, email, phone);
        resume.setName(name);

        // 4. Extract Skills
        List<Skill> skills = extractSkills(text, resume);
        resume.setSkills(skills);

        // 5. Extract Education
        List<Education> education = extractEducation(text, resume);
        resume.setEducation(education);

        // 6. Extract Experience
        List<Experience> experience = extractExperience(text, resume);
        resume.setExperience(experience);

        return resume;
    }

    private String extractEmail(String text) {
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
        Matcher matcher = emailPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    private String extractPhone(String text) {
        // Match numbers like: +1-123-456-7890, (123) 456-7890, 1234567890, etc.
        Pattern phonePattern = Pattern.compile("(\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}");
        Matcher matcher = phonePattern.matcher(text);
        if (matcher.find()) {
            return matcher.group().trim();
        }
        return "";
    }

    private String extractName(String text, String email, String phone) {
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            // Skip line if it contains email, phone, or generic title keywords
            if ((!email.isEmpty() && line.contains(email)) || 
                (!phone.isEmpty() && line.contains(phone)) ||
                line.toLowerCase().contains("resume") ||
                line.toLowerCase().contains("curriculum") ||
                line.toLowerCase().contains("cv") ||
                line.toLowerCase().contains("page") ||
                line.toLowerCase().contains("portfolio")) {
                continue;
            }

            // A name is typically 2 to 4 words
            String[] words = line.split("\\s+");
            if (words.length >= 2 && words.length <= 4) {
                // Check that it only contains alphabetical characters (plus standard space/dot)
                if (line.matches("^[a-zA-Z\\s\\.]+$")) {
                    return line;
                }
            }
        }
        return "Unknown Candidate";
    }

    private List<Skill> extractSkills(String text, Resume resume) {
        List<Skill> list = new ArrayList<>();
        String lowerText = text.toLowerCase();
        
        // Find matching skills (case-insensitive boundary match)
        for (String skillName : COMMON_SKILLS) {
            // Use word boundaries so "Java" doesn't match "JavaScript"
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(skillName.toLowerCase()) + "\\b");
            if (pattern.matcher(lowerText).find()) {
                Skill skill = new Skill();
                skill.setName(skillName);
                skill.setResume(resume);
                list.add(skill);
            }
        }
        return list;
    }

    private List<Education> extractEducation(String text, Resume resume) {
        List<Education> list = new ArrayList<>();
        String[] lines = text.split("\\r?\\n");
        
        boolean inEducationSection = false;
        int linesProcessed = 0;
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String lowerLine = line.toLowerCase();
            
            // Detect Section Start
            if (lowerLine.contains("education") || lowerLine.contains("academic qualification") || lowerLine.contains("academic profile")) {
                inEducationSection = true;
                linesProcessed = 0;
                continue;
            }
            
            if (inEducationSection) {
                // If we see another major heading, stop parsing education
                if (linesProcessed > 0 && (lowerLine.contains("experience") || lowerLine.contains("skills") || lowerLine.contains("projects") || lowerLine.contains("certifications") || lowerLine.contains("languages"))) {
                    inEducationSection = false;
                    continue;
                }
                
                // Heuristic: look for degrees
                if (lowerLine.contains("bachelor") || lowerLine.contains("master") || lowerLine.contains("doctor") ||
                    lowerLine.contains("b.tech") || lowerLine.contains("b.e") || lowerLine.contains("m.tech") || 
                    lowerLine.contains("b.sc") || lowerLine.contains("m.sc") || lowerLine.contains("phd") || 
                    lowerLine.contains("degree") || lowerLine.contains("university") || lowerLine.contains("college") || 
                    lowerLine.contains("school") || lowerLine.contains("diploma")) {
                    
                    Education edu = new Education();
                    edu.setResume(resume);
                    
                    // Parse degree
                    String degree = "Degree";
                    if (lowerLine.contains("b.tech") || lowerLine.contains("bachelor of technology")) degree = "B.Tech";
                    else if (lowerLine.contains("b.e") || lowerLine.contains("bachelor of engineering")) degree = "B.E.";
                    else if (lowerLine.contains("m.tech") || lowerLine.contains("master of technology")) degree = "M.Tech";
                    else if (lowerLine.contains("b.sc") || lowerLine.contains("bachelor of science")) degree = "B.Sc.";
                    else if (lowerLine.contains("m.sc") || lowerLine.contains("master of science")) degree = "M.Sc.";
                    else if (lowerLine.contains("phd") || lowerLine.contains("doctor of philosophy")) degree = "Ph.D.";
                    else if (lowerLine.contains("master")) degree = "Master's Degree";
                    else if (lowerLine.contains("bachelor")) degree = "Bachelor's Degree";
                    edu.setDegree(degree);

                    // Try to guess institution from line, else use line content
                    String inst = line;
                    if (line.toLowerCase().contains("at ")) {
                        inst = line.substring(line.toLowerCase().indexOf("at ") + 3).trim();
                    } else if (line.toLowerCase().contains("from ")) {
                        inst = line.substring(line.toLowerCase().indexOf("from ") + 5).trim();
                    }
                    // Limit length
                    if (inst.length() > 100) {
                        inst = inst.substring(0, 100);
                    }
                    edu.setInstitution(inst);
                    
                    // Try to find duration (e.g. 2018 - 2022 or 2015-2019)
                    Pattern yearPattern = Pattern.compile("(\\b20\\d{2}\\b)");
                    Matcher yearMatcher = yearPattern.matcher(line);
                    List<String> years = new ArrayList<>();
                    while (yearMatcher.find()) {
                        years.add(yearMatcher.group());
                    }
                    if (years.size() >= 2) {
                        edu.setDuration(years.get(0) + " - " + years.get(1));
                    } else if (years.size() == 1) {
                        edu.setDuration(years.get(0));
                    } else {
                        edu.setDuration("N/A");
                    }
                    
                    list.add(edu);
                }
                
                linesProcessed++;
                // Limit to scanning max 15 lines of education
                if (linesProcessed > 15) {
                    inEducationSection = false;
                }
            }
        }
        
        // Add a default if nothing is found
        if (list.isEmpty()) {
            Education edu = new Education();
            edu.setDegree("Bachelor's Degree (Auto-detected)");
            edu.setInstitution("University / College");
            edu.setDuration("N/A");
            edu.setResume(resume);
            list.add(edu);
        }
        
        return list;
    }

    private List<Experience> extractExperience(String text, Resume resume) {
        List<Experience> list = new ArrayList<>();
        String[] lines = text.split("\\r?\\n");
        
        boolean inExperienceSection = false;
        int linesProcessed = 0;
        Experience currentExp = null;
        StringBuilder descBuilder = new StringBuilder();
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            String lowerLine = line.toLowerCase();
            
            // Detect Section Start
            if (lowerLine.contains("experience") || lowerLine.contains("work history") || lowerLine.contains("employment history") || lowerLine.contains("professional background")) {
                inExperienceSection = true;
                linesProcessed = 0;
                continue;
            }
            
            if (inExperienceSection) {
                // If we see another major heading, stop experience parsing
                if (linesProcessed > 0 && (lowerLine.contains("education") || lowerLine.contains("skills") || lowerLine.contains("projects") || lowerLine.contains("certifications") || lowerLine.contains("languages"))) {
                    if (currentExp != null) {
                        currentExp.setDescription(descBuilder.toString().trim());
                        list.add(currentExp);
                        currentExp = null;
                    }
                    inExperienceSection = false;
                    continue;
                }
                
                // Heuristic: A line with a year range and a company/role keyword or just standard title structure
                boolean hasYearRange = lowerLine.matches(".*(20\\d{2}\\s*[-–]\\s*(20\\d{2}|present|current)).*") || 
                                       lowerLine.matches(".*\\b(20\\d{2})\\b.*") && (lowerLine.contains("engineer") || lowerLine.contains("developer") || lowerLine.contains("intern") || lowerLine.contains("analyst") || lowerLine.contains("manager"));
                
                if (hasYearRange && (lowerLine.contains("engineer") || lowerLine.contains("developer") || lowerLine.contains("intern") || lowerLine.contains("manager") || lowerLine.contains("analyst") || lowerLine.contains("consultant") || lowerLine.contains("at") || lowerLine.contains("for") || line.contains(","))) {
                    
                    if (currentExp != null) {
                        currentExp.setDescription(descBuilder.toString().trim());
                        list.add(currentExp);
                        descBuilder = new StringBuilder();
                    }
                    
                    currentExp = new Experience();
                    currentExp.setResume(resume);
                    
                    String role = "Software Engineer";
                    if (lowerLine.contains("senior")) {
                        if (lowerLine.contains("developer")) role = "Senior Developer";
                        else role = "Senior Software Engineer";
                    } else if (lowerLine.contains("intern")) {
                        role = "Software Engineer Intern";
                    } else if (lowerLine.contains("manager")) {
                        role = "Project Manager";
                    } else if (lowerLine.contains("developer")) {
                        role = "Full Stack Developer";
                    } else if (lowerLine.contains("analyst")) {
                        role = "Systems Analyst";
                    } else {
                        String[] words = line.split("\\s+");
                        if (words.length >= 2) {
                            role = words[0] + " " + words[1];
                        }
                    }
                    currentExp.setRole(role);
                    
                    String company = "Company";
                    if (line.contains(" at ")) {
                        company = line.substring(line.indexOf(" at ") + 4).split(",|\\(|-")[0].trim();
                    } else if (line.contains(" - ")) {
                        company = line.split(" - ")[0].trim();
                    } else {
                        company = line.split(",|\\(|-")[0].trim();
                    }
                    if (company.length() > 50) {
                        company = company.substring(0, 50);
                    }
                    currentExp.setCompany(company);
                    
                    Pattern durationPattern = Pattern.compile("(\\b20\\d{2}\\s*[-–]\\s*(?:20\\d{2}|present|current|now|active)\\b)", Pattern.CASE_INSENSITIVE);
                    Matcher durationMatcher = durationPattern.matcher(line);
                    if (durationMatcher.find()) {
                        currentExp.setDuration(durationMatcher.group());
                    } else {
                        currentExp.setDuration("N/A");
                    }
                } else {
                    if (currentExp != null) {
                        descBuilder.append(line).append("\n");
                    }
                }
                
                linesProcessed++;
                if (linesProcessed > 40) {
                    break;
                }
            }
        }
        
        if (currentExp != null) {
            currentExp.setDescription(descBuilder.toString().trim());
            list.add(currentExp);
        }
        
        if (list.isEmpty()) {
            Experience exp = new Experience();
            exp.setCompany("Company Name (Auto-detected)");
            exp.setRole("Software Professional");
            exp.setDuration("N/A");
            exp.setDescription("Responsibilities and achievements...");
            exp.setResume(resume);
            list.add(exp);
        }
        
        return list;
    }
}
