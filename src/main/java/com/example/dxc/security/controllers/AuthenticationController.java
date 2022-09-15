package com.example.dxc.security.controllers;

import com.example.dxc.security.DTO.JwtRequest;
import com.example.dxc.security.DTO.JwtResponse;
import com.example.dxc.security.utils.JWTUtility;
import com.example.dxc.security.service.implementation.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/token")
public class AuthenticationController {

    @Autowired
    private JWTUtility jwtUtility;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private MyUserDetailsService userDetailsService;

    @GetMapping
    public ResponseEntity<JwtResponse> authenticate(@RequestBody JwtRequest jwtRequest) throws Exception {
        try {
            //Authenticating the credentials.
            //Authentication is done by AuthenticationManager.
            //AuthenticationManager needs an instance of Authentication holding credentials.
            //UsernamePasswordAuthenticationToken is an implementation of Authentication interface.
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            jwtRequest.getUsername(),
                            jwtRequest.getPassword()
                    )
            );
        }
        catch (BadCredentialsException ex){
            // This will be triggered if authentication fails.
            // Username or Password is incorrect.
            throw new Exception("User Credentials are incorrect.");
        }
        // We have authenticated the user based on credentials in above try block.
        // Now we have to generate a token and send it back in response.
        // Get the UserDetails for respective user and generate a token using the JwtUtil.
        final UserDetails userDetails = this.userDetailsService.loadUserByUsername(jwtRequest.getUsername());
        final String token = jwtUtility.generateToken(userDetails);
        return new ResponseEntity<>(new JwtResponse(token), HttpStatus.OK);
    }
}
