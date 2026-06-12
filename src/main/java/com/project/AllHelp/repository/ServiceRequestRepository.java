package com.project.AllHelp.repository;

import com.project.AllHelp.entity.RequestStatus;
import com.project.AllHelp.entity.ServiceRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long>, JpaSpecificationExecutor<ServiceRequest> {
    List<ServiceRequest> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<ServiceRequest> findByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, RequestStatus status);

    long countByStatus(RequestStatus status);

    @EntityGraph(attributePaths = "user")
    List<ServiceRequest> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = "user")
    List<ServiceRequest> findByStatusOrderByCreatedAtDesc(RequestStatus status);

    @Query("select request from ServiceRequest request join fetch request.user where request.id = :id")
    Optional<ServiceRequest> findWithUserById(@Param("id") Long id);
}
