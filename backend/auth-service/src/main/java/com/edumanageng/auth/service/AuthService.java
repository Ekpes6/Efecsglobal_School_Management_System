package com.edumanageng.auth.service;

import com.edumanageng.auth.dto.*;
import com.edumanageng.auth.entity.Role;
import com.edumanageng.auth.entity.User;
import com.edumanageng.auth.exception.AuthException;
import com.edumanageng.auth.repository.RoleRepository;
import com.edumanageng.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("Email already registered");
        }
        if (request.getPhoneNumber() != null && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AuthException("Phone number already registered");
        }

        Role role = roleRepository.findByName(request.getRole() != null ? request.getRole() : Role.RoleName.GUARDIAN)
            .orElseThrow(() -> new AuthException("Role not found"));

        User user = User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .phoneNumber(request.getPhoneNumber())
            .password(passwordEncoder.encode(request.getPassword()))
            .schoolId(request.getSchoolId())
            .status(User.UserStatus.ACTIVE)
            .emailVerified(false)
            .phoneVerified(false)
            .roles(Set.of(role))
            .build();

        user = userRepository.save(Objects.requireNonNull(user));
        log.info("User registered: {}", user.getEmail());
        return generateAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new AuthException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new AuthException("User not found"));

        if (user.getStatus() == User.UserStatus.SUSPENDED) {
            throw new AuthException("Account has been suspended. Contact support.");
        }
        if (user.getStatus() == User.UserStatus.INACTIVE) {
            throw new AuthException("Account is inactive. Contact your school administrator.");
        }

        return generateAuthResponse(user);
    }

    public AuthResponse refreshToken(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new AuthException("Invalid refresh token"));

        if (!jwtService.isTokenValid(refreshToken, new org.springframework.security.core.userdetails.User(
            user.getEmail(), user.getPassword(), java.util.Collections.emptyList()))) {
            throw new AuthException("Refresh token expired");
        }

        return generateAuthResponse(user);
    }

    @Transactional
    public void logout(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setRefreshToken(null);
            userRepository.save(user);
        });
    }

    public UserInfoResponse getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new AuthException("User not found"));
        return mapToUserInfo(user);
    }

    private AuthResponse generateAuthResponse(User user) {
        Map<String, Object> claims = new HashMap<>();
        String roleName = user.getRoles().stream()
            .findFirst()
            .map(r -> r.getName().name())
            .orElse("GUARDIAN");
        claims.put("role", roleName);
        claims.put("schoolId", user.getSchoolId());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());

        var userDetails = new org.springframework.security.core.userdetails.User(
            user.getEmail(), user.getPassword(), java.util.Collections.emptyList());

        String accessToken = jwtService.generateAccessToken(userDetails, claims);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .userId(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(roleName)
            .schoolId(user.getSchoolId())
            .build();
    }

    private UserInfoResponse mapToUserInfo(User user) {
        return UserInfoResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .phoneNumber(user.getPhoneNumber())
            .schoolId(user.getSchoolId())
            .status(user.getStatus().name())
            .emailVerified(user.isEmailVerified())
            .role(user.getRoles().stream().findFirst().map(r -> r.getName().name()).orElse(""))
            .build();
    }
}
