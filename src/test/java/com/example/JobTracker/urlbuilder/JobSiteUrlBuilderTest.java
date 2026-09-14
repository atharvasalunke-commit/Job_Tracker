package com.example.JobTracker.urlbuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class JobSiteUrlBuilderTest {

    private JobSiteUrlBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new JobSiteUrlBuilder();
        builder.init();
    }

    @Test
    void checkIfUnsupportedDomainThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> builder.buildUrl("naukri", List.of("java")));
    }

    @Test
    void checkIfSkillCasingIsIgnored() {
        String lowerCaseUrl = builder.buildUrl("internshala", List.of("python"));
        String mixedCaseUrl = builder.buildUrl("internshala", List.of("Python"));

        assertEquals(lowerCaseUrl, mixedCaseUrl);
    }
}
