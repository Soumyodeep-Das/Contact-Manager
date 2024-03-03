package com.example.smartcontacts.SmartConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.smartcontacts.DAO.UserRepository;
import com.example.smartcontacts.Entities.User;

public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // throw new UnsupportedOperationException("Unimplemented method 'loadUserByUsername'");
        // fetching user data
        User user = userRepository.getUserByUserName(username);

        if(user==null)
            throw new UsernameNotFoundException("Could not found the user");

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        return customUserDetails;
    }
    
}
