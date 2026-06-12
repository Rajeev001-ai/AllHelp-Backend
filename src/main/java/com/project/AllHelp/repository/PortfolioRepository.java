package com.project.AllHelp.repository;

import com.project.AllHelp.entity.Portfolio;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByWorkerUserIdOrderByCreatedAtDesc(Long workerUserId);

    List<Portfolio> findByWorkerIdOrderByCreatedAtDesc(Long workerId);

    @EntityGraph(attributePaths = {"worker", "worker.user"})
    Optional<Portfolio> findByIdAndWorkerUserId(Long id, Long workerUserId);
}
