package com.saurabh.security.service.implementation;

import com.saurabh.security.entities.MyUserDetails;
import com.saurabh.security.entities.User;
import com.saurabh.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = this.userRepository.findByEmail(email);
        if(user.isEmpty())
            throw new UsernameNotFoundException("Not a valid Username : "+email);
        return new MyUserDetails(user.get());
    }
}
