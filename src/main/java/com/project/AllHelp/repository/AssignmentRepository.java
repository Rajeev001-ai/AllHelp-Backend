package com.project.AllHelp.repository;

import com.project.AllHelp.entity.Assignment;
import com.project.AllHelp.entity.AssignmentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    @EntityGraph(attributePaths = {"serviceRequest", "serviceRequest.user", "worker", "worker.user", "assignedByAdmin"})
    List<Assignment> findAllByOrderByAssignedAtDesc();

    @EntityGraph(attributePaths = {"serviceRequest", "serviceRequest.user", "worker", "worker.user", "assignedByAdmin"})
    List<Assignment> findByAssignmentStatusOrderByAssignedAtDesc(AssignmentStatus status);

    @EntityGraph(attributePaths = {"serviceRequest", "serviceRequest.user", "worker", "worker.user", "assignedByAdmin"})
    Optional<Assignment> findFirstByServiceRequestIdAndAssignmentStatusOrderByAssignedAtDesc(Long serviceRequestId, AssignmentStatus status);

    @EntityGraph(attributePaths = {"serviceRequest", "serviceRequest.user", "worker", "worker.user", "assignedByAdmin"})
    Optional<Assignment> findFirstByServiceRequestIdOrderByAssignedAtDesc(Long serviceRequestId);

    @EntityGraph(attributePaths = {"serviceRequest", "serviceRequest.user", "worker", "worker.user", "assignedByAdmin"})
    @Query("select assignment from Assignment assignment join fetch assignment.serviceRequest request join fetch request.user join fetch assignment.worker worker join fetch worker.user left join fetch assignment.assignedByAdmin where assignment.id = :id")
    Optional<Assignment> findWithDetailsById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"serviceRequest", "serviceRequest.user", "worker", "worker.user", "assignedByAdmin"})
    List<Assignment> findByWorkerUserIdOrderByAssignedAtDesc(Long workerUserId);

    @EntityGraph(attributePaths = {"serviceRequest", "serviceRequest.user", "worker", "worker.user", "assignedByAdmin"})
    List<Assignment> findByWorkerUserIdAndAssignmentStatusOrderByAssignedAtDesc(Long workerUserId, AssignmentStatus status);

    @EntityGraph(attributePaths = {"serviceRequest", "serviceRequest.user", "worker", "worker.user", "assignedByAdmin"})
    Optional<Assignment> findByIdAndWorkerUserId(Long id, Long workerUserId);

    long countByWorkerUserId(Long workerUserId);

    long countByWorkerUserIdAndAssignmentStatus(Long workerUserId, AssignmentStatus status);

    long countByAssignmentStatusIn(List<AssignmentStatus> statuses);
}
