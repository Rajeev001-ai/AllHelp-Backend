package com.project.AllHelp.repository;

import com.project.AllHelp.entity.Activity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByRequestIdOrderByCreatedAtAsc(Long requestId);
}
