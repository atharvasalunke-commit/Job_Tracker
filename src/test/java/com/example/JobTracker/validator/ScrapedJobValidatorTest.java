package com.example.JobTracker.validator;

import com.example.JobTracker.dto.ScrapedJobDto;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ScrapedJobValidatorTest {

    private final ScrapedJobValidator validator = new ScrapedJobValidator();

    @Test
    void checkIfWellFormedJobIsValid() {
        ScrapedJobDto job = new ScrapedJobDto();
        job.setApplication_url("https://careers.google.com");
        job.setJob_title("Software Engineer");
        job.setCompany_name("Google");

        assertTrue(validator.isValid(job, new ArrayList<>(), new HashMap<>()));
    }

    @Test
    void  checkIfBadUrlIsInvalid() {
        ScrapedJobDto job = new ScrapedJobDto();
        job.setApplication_url("not-a-real-url");
        job.setJob_title("Software Engineer");
        job.setCompany_name("Google");
        Map<String, String> invalidUrls = new HashMap<>();

        assertFalse(validator.isValid(job, new ArrayList<>(), invalidUrls));
        assertTrue(invalidUrls.containsKey("Invalid Url"));
    }

    @Test
    void checkIfLongJobTitleIsInvalid() {
        ScrapedJobDto job = new ScrapedJobDto();
        job.setApplication_url("https://careers.google.com");
        job.setJob_title("a".repeat(101));
        job.setCompany_name("Google");
        List<String> reconfigured = new ArrayList<>();

        assertFalse(validator.isValid(job, reconfigured, new HashMap<>()));
        assertEquals(1, reconfigured.size());
    }

    @Test
    void checkIfEmptyCompanyNameIsInvalid() {
        ScrapedJobDto job = new ScrapedJobDto();
        job.setApplication_url("https://careers.google.com");
        job.setJob_title("Software Engineer");
        job.setCompany_name("");

        assertFalse(validator.isValid(job, new ArrayList<>(), new HashMap<>()));
    }
}
