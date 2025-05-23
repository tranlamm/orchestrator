package com.lamt2.orchestrator.service.security;

import com.lamt2.orchestrator.entity.UserEntity;
import com.lamt2.orchestrator.model.security.CustomUserDetails;
import com.lamt2.orchestrator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        List<String> roles = userRepository.findAllUserRole(username);
        userEntity.setRoles(roles);
        return new CustomUserDetails(userEntity);
    }
}
