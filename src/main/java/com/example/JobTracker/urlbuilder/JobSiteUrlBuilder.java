package com.example.JobTracker.urlbuilder;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component

public class JobSiteUrlBuilder {
    private Map<String, Map<String, String>> siteSlugs;
    private final Map<String, String> siteUrls = Map.of("authenticjobs", "https://authenticjobs.com/?s=%s", "internshala", "https://internshala.com/internships/keywords-%s/");

    @PostConstruct
    public void init() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            try (InputStream inputStream = getClass().getResourceAsStream("/site-slugs.json")) {
                if (inputStream == null) {
                    throw new IllegalStateException("site-slugs.json not found");
                }
                siteSlugs = mapper.readValue(inputStream, new TypeReference<Map<String, Map<String, String>>>() {});
            }
} catch (Exception e) {
            throw new IllegalStateException("Failed to load site-slugs.json", e);
        }
    }

    public String buildUrl(String domain, List<String> userSkills) {
        String urlTemplate = siteUrls.get(domain);
        if (urlTemplate == null) {
            throw new IllegalArgumentException("Unsupported job site: " + domain);
        }
        String skill = "developer";
        if (userSkills != null && !userSkills.isEmpty()) {
            String requestedSkill = userSkills.getFirst().toLowerCase();
            Map<String, String> domainSlugs = siteSlugs.get(domain);
            if (domainSlugs != null && domainSlugs.containsKey(requestedSkill)) {
                skill = domainSlugs.get(requestedSkill);
}
            else {
                skill = requestedSkill;
            }
        }
        return String.format(urlTemplate, skill);
    }
}