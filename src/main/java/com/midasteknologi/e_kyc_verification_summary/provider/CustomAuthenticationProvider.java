package com.midasteknologi.e_kyc_verification_summary.provider;

import com.midasteknologi.e_kyc_verification_summary.entity.AdminUser;
import com.midasteknologi.e_kyc_verification_summary.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final AdminUserRepository adminUserRepository;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String password = String.valueOf(authentication.getCredentials());

        if (username == null || password == null) {
            return null;
        }

        return authenticateWithUserRole(username, password);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private UsernamePasswordAuthenticationToken authenticateWithUserRole(String username, String password) {
        Optional<AdminUser> adminUserOptional = adminUserRepository.findByEmail(username);

        if (adminUserOptional.isEmpty()) {
            return null;
        }

        final List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        final UserDetails userDetails = new User(username, password, grantedAuthorities);
        return new UsernamePasswordAuthenticationToken(userDetails, password, grantedAuthorities);
    }
}
