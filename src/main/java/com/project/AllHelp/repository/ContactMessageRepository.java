package com.project.AllHelp.repository;

import com.project.AllHelp.entity.ContactMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    List<ContactMessage> findAllByOrderByCreatedAtDesc();

    long countByReadFalse();
}
