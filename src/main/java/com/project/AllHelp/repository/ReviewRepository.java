package com.project.AllHelp.repository;

import com.project.AllHelp.entity.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByServiceRequestId(Long serviceRequestId);

    long countByWorkerId(Long workerId);

    @Query("select coalesce(avg(review.rating), 0) from Review review where review.worker.id = :workerId")
    double averageRatingByWorkerId(@Param("workerId") Long workerId);

    @EntityGraph(attributePaths = {"serviceRequest", "serviceRequest.user", "user", "worker", "worker.user"})
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

    @EntityGraph(attributePaths = {"serviceRequest", "serviceRequest.user", "user", "worker", "worker.user"})
    List<Review> findByWorkerUserIdOrderByCreatedAtDesc(Long workerUserId);

    @EntityGraph(attributePaths = {"serviceRequest", "serviceRequest.user", "user", "worker", "worker.user"})
    Optional<Review> findByServiceRequestId(Long serviceRequestId);
}
