package com.project.AllHelp.service;

import com.project.AllHelp.dto.AuthResponse;
import com.project.AllHelp.dto.LoginRequest;
import com.project.AllHelp.dto.RegisterRequest;
import com.project.AllHelp.entity.AppUser;
import com.project.AllHelp.entity.Role;
import com.project.AllHelp.exception.ApiException;
import com.project.AllHelp.repository.AppUserRepository;
import com.project.AllHelp.security.JwtService;
import com.project.AllHelp.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final WorkerProfileService workerProfileService;
    private final String adminUsername;
    private final String adminPassword;

    public AuthService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            WorkerProfileService workerProfileService,
            @Value("${app.admin.username}") String adminUsername,
            @Value("${app.admin.password}") String adminPassword
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.workerProfileService = workerProfileService;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    public AuthResponse register(RegisterRequest request) {
        if (request.role() == Role.ADMIN) {
            throw new ApiException("Admin registration is not allowed", HttpStatus.BAD_REQUEST);
        }
        if (appUserRepository.existsByEmail(request.email().toLowerCase())) {
            throw new ApiException("Email is already registered", HttpStatus.CONFLICT);
        }
        if (appUserRepository.existsByPhone(request.phone())) {
            throw new ApiException("Phone number is already registered", HttpStatus.CONFLICT);
        }

        AppUser user = new AppUser();
        user.setFullName(request.fullName());
        user.setEmail(request.email().toLowerCase());
        user.setPhone(request.phone());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());

        AppUser savedUser = appUserRepository.save(user);
        if (savedUser.getRole() == Role.WORKER) {
            workerProfileService.createDefaultProfile(savedUser);
        }
        UserPrincipal principal = UserPrincipal.fromUser(savedUser);
        return response(principal, "Registration successful");
    }

    public AuthResponse login(LoginRequest request) {
        if (adminUsername.equals(request.emailOrUsername()) && adminPassword.equals(request.password())) {
            return response(UserPrincipal.admin(adminUsername, adminPassword), "Admin login successful");
        }

        AppUser user = appUserRepository.findByEmail(request.emailOrUsername().toLowerCase())
                .orElseThrow(() -> new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        return response(UserPrincipal.fromUser(user), "Login successful");
    }

    private AuthResponse response(UserPrincipal principal, String message) {
        String token = jwtService.generateToken(principal);
        return new AuthResponse(
                principal.getId(),
                principal.getFullName(),
                principal.getUsername(),
                principal.getRole(),
                token,
                "Bearer",
                message
        );
    }
}
