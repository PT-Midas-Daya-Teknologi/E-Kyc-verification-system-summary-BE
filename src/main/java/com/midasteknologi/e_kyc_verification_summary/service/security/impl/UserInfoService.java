package com.midasteknologi.e_kyc_verification_summary.service.security.impl;

import com.midasteknologi.e_kyc_verification_summary.entity.AdminUser;
import com.midasteknologi.e_kyc_verification_summary.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoService implements UserDetailsService {

    private final AdminUserRepository adminUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AdminUser> userInfoDetailsOptional = adminUserRepository.findByEmail(username);

        if (userInfoDetailsOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        log.info("password from db {}", userInfoDetailsOptional.get().getPassword());
        return new User(userInfoDetailsOptional.get().getEmail(), userInfoDetailsOptional.get().getPassword(), List.of(new GrantedAuthority() {
            @Override
            public @Nullable String getAuthority() {
                return "ADMIN";
            }
        }));
    }
}
