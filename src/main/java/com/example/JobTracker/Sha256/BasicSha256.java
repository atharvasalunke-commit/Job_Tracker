package com.example.JobTracker.Sha256;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

@Component

public class BasicSha256 {
    public String toSha256(String data) throws Exception {
        // 1. Get the SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        // 2. Perform the hash
        byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        // 3. Convert the raw bytes to a readable Hex string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        System.out.println(hexString.toString());
        return hexString.toString();
    }
}
