package com.parser.resume.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parser.resume.model.*;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GeminiParserService {

    private final Tika tika = new Tika();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    // Full list of common skills for regex-based extraction (from original ResumeParserService)
    private static final List<String> COMMON_SKILLS = Arrays.asList(
        "Java", "Spring Boot", "Spring", "Hibernate", "JPA", "SQL", "MySQL", "PostgreSQL", "Oracle",
        "MongoDB", "Redis", "Cassandra", "React", "Angular", "Vue", "JavaScript", "TypeScript",
        "HTML", "CSS", "Tailwind", "Node.js", "Express", "Python", "Django", "Flask", "C++", "C#",
        "Ruby", "Rails", "Go", "Golang", "Rust", "Swift", "Kotlin", "Docker", "Kubernetes", "AWS",
        "Azure", "GCP", "Git", "GitHub", "CI/CD", "Jenkins", "JUnit", "Mockito", "Selenium", "REST API",
        "GraphQL", "Microservices", "Kafka", "RabbitMQ", "Machine Learning", "Data Science", "Linux"
    );

    public Candidate parseResume(MultipartFile file) throws Exception {
        String text;
        try (InputStream stream = file.getInputStream()) {
            text = tika.parseToString(stream);
        }

        Candidate candidate = null;
        if (geminiApiKey != null && !geminiApiKey.trim().isEmpty()) {
            candidate = parseWithGemini(text);
        } else {
            candidate = parseWithFallback(text);
        }

        candidate.setResumeFileName(file.getOriginalFilename());
        candidate.setResumeFileContentType(file.getContentType());
        candidate.setResumeFile(file.getBytes());

        // Bidirectional links
        linkChildEntities(candidate);

        return candidate;
    }

    private Candidate parseWithGemini(String rawText) throws Exception {
        String prompt = "Parse the following raw text from a resume and extract the candidate information as structured JSON. " +
                "Ensure you return a JSON object with the following schema exactly (no extra markdown around the JSON, just the JSON string):\n" +
                "{\n" +
                "  \"fullName\": \"string\",\n" +
                "  \"email\": \"string\",\n" +
                "  \"phone\": \"string\",\n" +
                "  \"linkedin\": \"string\",\n" +
                "  \"github\": \"string\",\n" +
                "  \"location\": \"string\",\n" +
                "  \"summary\": \"string\",\n" +
                "  \"skills\": [\"string\"],\n" +
                "  \"softSkills\": [\"string\"],\n" +
                "  \"languages\": [\"string\"],\n" +
                "  \"certifications\": [\"string\"],\n" +
                "  \"projects\": [{\"title\":\"string\", \"description\":\"string\", \"technologies\":\"string\"}],\n" +
                "  \"education\": [{\"degree\":\"string\", \"institution\":\"string\", \"duration\":\"string\"}],\n" +
                "  \"experience\": [{\"company\":\"string\", \"role\":\"string\", \"duration\":\"string\", \"description\":\"string\"}]\n" +
                "}\n\n" +
                "Resume Text:\n" + rawText;

        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> requestBodyMap = Map.of(
                "contents", List.of(content),
                "generationConfig", Map.of("responseMimeType", "application/json")
        );

        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            System.err.println("Gemini API Error: " + response.body());
            throw new Exception("Gemini API returned error: " + response.statusCode());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode candidatesNode = root.path("candidates");
        if (candidatesNode.isMissingNode() || candidatesNode.isEmpty()) {
            throw new Exception("No output from Gemini");
        }
        
        String jsonResult = candidatesNode.get(0).path("content").path("parts").get(0).path("text").asText();
        
        Candidate candidate = parseJsonToCandidate(jsonResult);
        candidate.setRawJson(jsonResult);
        return candidate;
    }

    private Candidate parseJsonToCandidate(String json) {
        Candidate candidate = new Candidate();
        try {
            JsonNode root = objectMapper.readTree(json);
            candidate.setFullName(root.path("fullName").asText(null));
            candidate.setEmail(root.path("email").asText(null));
            candidate.setPhone(root.path("phone").asText(null));
            candidate.setLinkedin(root.path("linkedin").asText(null));
            candidate.setGithub(root.path("github").asText(null));
            candidate.setLocation(root.path("location").asText(null));
            candidate.setSummary(root.path("summary").asText(null));
            
            if (root.has("skills") && root.get("skills").isArray()) {
                List<CandidateSkill> skills = new ArrayList<>();
                for (JsonNode n : root.get("skills")) {
                    skills.add(new CandidateSkill(n.asText(), candidate));
                }
                candidate.setSkills(skills);
            }
            if (root.has("softSkills") && root.get("softSkills").isArray()) {
                List<CandidateSoftSkill> sSkills = new ArrayList<>();
                for (JsonNode n : root.get("softSkills")) {
                    sSkills.add(new CandidateSoftSkill(n.asText(), candidate));
                }
                candidate.setSoftSkills(sSkills);
            }
            if (root.has("languages") && root.get("languages").isArray()) {
                List<CandidateLanguage> langs = new ArrayList<>();
                for (JsonNode n : root.get("languages")) {
                    langs.add(new CandidateLanguage(n.asText(), candidate));
                }
                candidate.setLanguages(langs);
            }
            if (root.has("certifications") && root.get("certifications").isArray()) {
                List<CandidateCertification> certs = new ArrayList<>();
                for (JsonNode n : root.get("certifications")) {
                    certs.add(new CandidateCertification(n.asText(), candidate));
                }
                candidate.setCertifications(certs);
            }
            if (root.has("projects") && root.get("projects").isArray()) {
                List<CandidateProject> projects = new ArrayList<>();
                for (JsonNode n : root.get("projects")) {
                    CandidateProject p = new CandidateProject();
                    p.setTitle(n.path("title").asText(null));
                    p.setDescription(n.path("description").asText(null));
                    p.setTechnologies(n.path("technologies").asText(null));
                    p.setCandidate(candidate);
                    projects.add(p);
                }
                candidate.setProjects(projects);
            }
            if (root.has("education") && root.get("education").isArray()) {
                List<CandidateEducation> edus = new ArrayList<>();
                for (JsonNode n : root.get("education")) {
                    CandidateEducation e = new CandidateEducation();
                    e.setDegree(n.path("degree").asText(null));
                    e.setInstitution(n.path("institution").asText(null));
                    e.setDuration(n.path("duration").asText(null));
                    e.setCandidate(candidate);
                    edus.add(e);
                }
                candidate.setEducation(edus);
            }
            if (root.has("experience") && root.get("experience").isArray()) {
                List<CandidateExperience> exps = new ArrayList<>();
                for (JsonNode n : root.get("experience")) {
                    CandidateExperience e = new CandidateExperience();
                    e.setCompany(n.path("company").asText(null));
                    e.setRole(n.path("role").asText(null));
                    e.setDuration(n.path("duration").asText(null));
                    e.setDescription(n.path("description").asText(null));
                    e.setCandidate(candidate);
                    exps.add(e);
                }
                candidate.setExperience(exps);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return candidate;
    }

    // =========================================================================
    // FALLBACK: Full regex-based parser (ported from original ResumeParserService)
    // Used when no GEMINI_API_KEY is configured.
    // =========================================================================

    private Candidate parseWithFallback(String text) {
        Candidate candidate = new Candidate();
        candidate.setRawJson("{\"status\": \"Regex-parsed (no GEMINI_API_KEY). Set gemini.api.key for AI extraction.\"}");

        // 1. Extract Email
        String email = extractEmail(text);
        candidate.setEmail(email);

        // 2. Extract Phone
        String phone = extractPhone(text);
        candidate.setPhone(phone);

        // 3. Extract Name
        String name = extractName(text, email, phone);
        candidate.setFullName(name);

        // 4. Extract Skills
        List<CandidateSkill> skills = extractSkills(text, candidate);
        candidate.setSkills(skills);

        // 5. Extract Education
        List<CandidateEducation> education = extractEducation(text, candidate);
        candidate.setEducation(education);

        // 6. Extract Experience
        List<CandidateExperience> experience = extractExperience(text, candidate);
        candidate.setExperience(experience);

        return candidate;
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

    private List<CandidateSkill> extractSkills(String text, Candidate candidate) {
        List<CandidateSkill> list = new ArrayList<>();
        String lowerText = text.toLowerCase();

        // Find matching skills (case-insensitive boundary match)
        for (String skillName : COMMON_SKILLS) {
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(skillName.toLowerCase()) + "\\b");
            if (pattern.matcher(lowerText).find()) {
                list.add(new CandidateSkill(skillName, candidate));
            }
        }
        return list;
    }

    private List<CandidateEducation> extractEducation(String text, Candidate candidate) {
        List<CandidateEducation> list = new ArrayList<>();
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

                    CandidateEducation edu = new CandidateEducation();
                    edu.setCandidate(candidate);

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
                if (linesProcessed > 15) {
                    inEducationSection = false;
                }
            }
        }

        // Add a default if nothing is found
        if (list.isEmpty()) {
            CandidateEducation edu = new CandidateEducation();
            edu.setDegree("Bachelor's Degree (Auto-detected)");
            edu.setInstitution("University / College");
            edu.setDuration("N/A");
            edu.setCandidate(candidate);
            list.add(edu);
        }

        return list;
    }

    private List<CandidateExperience> extractExperience(String text, Candidate candidate) {
        List<CandidateExperience> list = new ArrayList<>();
        String[] lines = text.split("\\r?\\n");

        boolean inExperienceSection = false;
        int linesProcessed = 0;
        CandidateExperience currentExp = null;
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

                // Heuristic: A line with a year range and a company/role keyword
                boolean hasYearRange = lowerLine.matches(".*(20\\d{2}\\s*[-\u2013]\\s*(20\\d{2}|present|current)).*") ||
                                       lowerLine.matches(".*\\b(20\\d{2})\\b.*") && (lowerLine.contains("engineer") || lowerLine.contains("developer") || lowerLine.contains("intern") || lowerLine.contains("analyst") || lowerLine.contains("manager"));

                if (hasYearRange && (lowerLine.contains("engineer") || lowerLine.contains("developer") || lowerLine.contains("intern") || lowerLine.contains("manager") || lowerLine.contains("analyst") || lowerLine.contains("consultant") || lowerLine.contains("at") || lowerLine.contains("for") || line.contains(","))) {

                    if (currentExp != null) {
                        currentExp.setDescription(descBuilder.toString().trim());
                        list.add(currentExp);
                        descBuilder = new StringBuilder();
                    }

                    currentExp = new CandidateExperience();
                    currentExp.setCandidate(candidate);

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

                    Pattern durationPattern = Pattern.compile("(\\b20\\d{2}\\s*[-\u2013]\\s*(?:20\\d{2}|present|current|now|active)\\b)", Pattern.CASE_INSENSITIVE);
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
            CandidateExperience exp = new CandidateExperience();
            exp.setCompany("Company Name (Auto-detected)");
            exp.setRole("Software Professional");
            exp.setDuration("N/A");
            exp.setDescription("Responsibilities and achievements...");
            exp.setCandidate(candidate);
            list.add(exp);
        }

        return list;
    }

    private void linkChildEntities(Candidate candidate) {
        if (candidate.getSkills() != null) candidate.getSkills().forEach(s -> s.setCandidate(candidate));
        if (candidate.getSoftSkills() != null) candidate.getSoftSkills().forEach(s -> s.setCandidate(candidate));
        if (candidate.getLanguages() != null) candidate.getLanguages().forEach(s -> s.setCandidate(candidate));
        if (candidate.getCertifications() != null) candidate.getCertifications().forEach(s -> s.setCandidate(candidate));
        if (candidate.getProjects() != null) candidate.getProjects().forEach(s -> s.setCandidate(candidate));
        if (candidate.getEducation() != null) candidate.getEducation().forEach(s -> s.setCandidate(candidate));
        if (candidate.getExperience() != null) candidate.getExperience().forEach(s -> s.setCandidate(candidate));
    }
}
