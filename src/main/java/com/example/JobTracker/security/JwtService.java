package com.example.JobTracker.security;

import lombok.RequiredArgsConstructor;
import com.example.JobTracker.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service

public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    private SecretKey getSigningKey(){
        byte[] keyBytes= Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }
    public<T>T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims extraClaims=extractAllClaims(token);
        return claimsResolver.apply(extraClaims);
    }
    private Claims extractAllClaims(String token){
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    public String generateToken(User user){
        return generateToken(new HashMap<>(),user);
    }

    public String generateToken(HashMap<String,Object>Claims, User user){
        return Jwts.builder().claims(Claims).subject(user.getUsername()).issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis()+jwtExpiration)).signWith(getSigningKey()).compact();
    }
    private Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    public boolean isTokenValid(String token){
     if(extractExpiration(token).before(new Date())){
         return false;
     }
     return true;
    }
}
