package com.example.JobTracker.security;

import com.example.JobTracker.entity.User;
import com.example.JobTracker.globalexception.ResourceNotFound;
import com.example.JobTracker.repository.Repository1;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
   @Autowired
    private JwtService jwtservice;
    @Autowired
    private Repository1 repo;
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest Request, @NotNull HttpServletResponse Response, FilterChain filterchain) throws IOException, ServletException {
        final String authHeader = Request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterchain.doFilter(Request, Response);
            return;
        }
        String jwt = authHeader.substring(7);
        try {
            String username = jwtservice.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = repo.findByUsername(username).orElseThrow(()->new ResourceNotFound("User not found with this username"));
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole())));
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(Request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
            SecurityContextHolder.clearContext();
        }
        filterchain.doFilter(Request, Response);
    }

}
