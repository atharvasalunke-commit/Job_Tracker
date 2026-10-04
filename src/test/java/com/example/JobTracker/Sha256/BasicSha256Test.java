package com.example.JobTracker.Sha256;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasicSha256Test {

    @Test
    void toSha256ShouldReturnCorrectHash() throws Exception {
        BasicSha256 sha256 = new BasicSha256();
        String result = sha256.toSha256("hello");
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", result);
    }
    @Test
    void toSha256ShouldReturnDifferentHashForDifferentInput() throws Exception {
        BasicSha256 sha256 = new BasicSha256();
        assertNotEquals(sha256.toSha256("hello"), sha256.toSha256("world"));
    }
}
