package com.chattingo.config;

import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtValidator extends OncePerRequestFilter {

    private final SecretKey key;

    public JwtValidator(@Value("${jwt.secret}") String jwtSecret) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = request.getHeader("Authorization");

        if (jwt != null) {
            try {

                jwt = jwt.substring(7);

                Claims claim = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

                String username = String.valueOf(claim.get("email"));
                
                // Handle missing authorities claim - use empty list as default
                List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList("");

                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, auths);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                throw new BadCredentialsException("Invalid token recieved...");
            }
        }

        filterChain.doFilter(request, response);
    }

}
