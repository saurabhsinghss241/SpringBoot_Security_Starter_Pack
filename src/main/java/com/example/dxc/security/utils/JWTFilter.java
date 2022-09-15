package com.example.dxc.security.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// This is a custom filter responsible for intercepting incoming req.
// Check if the req has a header with authorization holding bearer token.
// If we have bearer token.
// Extract username, Get UserDetails for this username.
// validate token with respect to UserDetails.
// If token is valid.
// Create an UsernamePasswordAuthenticationToken which is an implementation of Authentication interface.
// And set Principal and authorities.
// Principal will be used to identify the user (is an instance of UserDetails)
// authorities will tell the high level permissions the user is granted (roles or scopes).
// Populate SecurityContextHolder with the Authentication object (UsernamePasswordAuthenticationToken)
// SecurityContextHolder - is where Spring Security stores the details of who is authenticated.

@Component
public class JWTFilter extends OncePerRequestFilter {
    @Autowired
    private JWTUtility jwtUtility;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")){
            token = authorizationHeader.substring(7);
            username = jwtUtility.getUsernameFromToken(token);
        }
        if(username!=null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if(jwtUtility.validateToken(token,userDetails)){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

                // Storing additional information related to this request.
                // Like ip,session-info etc. which we can refer in future based on our needs.
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        // We set the Authentication obj based on the token to SecurityContextHolder.
        // Our job done now we want this request to move on to next filter in filter chain.
        filterChain.doFilter(request,response);
    }
}
