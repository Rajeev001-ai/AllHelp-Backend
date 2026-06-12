package com.project.AllHelp.repository;

import com.project.AllHelp.entity.AppUser;
import com.project.AllHelp.entity.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByPhone(String phone);

    List<AppUser> findAllByOrderByCreatedAtDesc();

    long countByRole(Role role);
}
