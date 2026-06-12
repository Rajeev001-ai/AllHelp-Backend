package com.project.AllHelp.security;

import com.project.AllHelp.entity.AppUser;
import com.project.AllHelp.entity.Role;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String fullName;
    private final String email;
    private final String password;
    private final Role role;

    public UserPrincipal(Long id, String fullName, String email, String password, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static UserPrincipal fromUser(AppUser user) {
        return new UserPrincipal(user.getId(), user.getFullName(), user.getEmail(), user.getPassword(), user.getRole());
    }

    public static UserPrincipal admin(String username, String password) {
        return new UserPrincipal(null, "Admin", username, password, Role.ADMIN);
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
