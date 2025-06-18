package com.yourcompany.seaweedfs.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService implements UserDetailsService {

    private final Map<String, UserDetails> users = new ConcurrentHashMap<>();

    // 构造函数现在注入 PasswordEncoder
    public UserService(PasswordEncoder passwordEncoder) {
        // 在这里创建模拟用户
        users.put("admin", User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .authorities(new ArrayList<>())
                .build());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return user;
    }
}
