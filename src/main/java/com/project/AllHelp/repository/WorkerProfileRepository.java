package com.project.AllHelp.repository;

import com.project.AllHelp.entity.Availability;
import com.project.AllHelp.entity.WorkerProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkerProfileRepository extends JpaRepository<WorkerProfile, Long>, JpaSpecificationExecutor<WorkerProfile> {
    @EntityGraph(attributePaths = "user")
    List<WorkerProfile> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = "user")
    @Query("select worker from WorkerProfile worker join fetch worker.user where worker.id = :id")
    Optional<WorkerProfile> findWithUserById(@Param("id") Long id);

    @EntityGraph(attributePaths = "user")
    Optional<WorkerProfile> findByUserId(Long userId);

    long countByAvailability(Availability availability);
}
