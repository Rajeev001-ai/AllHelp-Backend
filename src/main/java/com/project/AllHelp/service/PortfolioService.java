package com.project.AllHelp.service;

import com.project.AllHelp.dto.PortfolioDto;
import com.project.AllHelp.dto.UploadResultDto;
import com.project.AllHelp.entity.Portfolio;
import com.project.AllHelp.entity.WorkerProfile;
import com.project.AllHelp.exception.ApiException;
import com.project.AllHelp.repository.PortfolioRepository;
import com.project.AllHelp.repository.WorkerProfileRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final MediaUploadService mediaUploadService;

    public PortfolioService(PortfolioRepository portfolioRepository, WorkerProfileRepository workerProfileRepository, MediaUploadService mediaUploadService) {
        this.portfolioRepository = portfolioRepository;
        this.workerProfileRepository = workerProfileRepository;
        this.mediaUploadService = mediaUploadService;
    }

    @Transactional
    public PortfolioDto create(Long workerUserId, MultipartFile image, String title, String description) {
        WorkerProfile worker = workerProfileRepository.findByUserId(workerUserId)
                .orElseThrow(() -> new ApiException("Worker profile not found", HttpStatus.NOT_FOUND));
        UploadResultDto upload = mediaUploadService.uploadImage(image, "allhelp/portfolio");
        Portfolio portfolio = new Portfolio();
        portfolio.setWorker(worker);
        portfolio.setImageUrl(upload.url());
        portfolio.setPublicId(upload.publicId());
        portfolio.setTitle(title);
        portfolio.setDescription(description);
        return toDto(portfolioRepository.save(portfolio));
    }

    @Transactional(readOnly = true)
    public List<PortfolioDto> getWorkerPortfolio(Long workerUserId) {
        return portfolioRepository.findByWorkerUserIdOrderByCreatedAtDesc(workerUserId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<PortfolioDto> getPortfolioByWorkerId(Long workerId) {
        return portfolioRepository.findByWorkerIdOrderByCreatedAtDesc(workerId).stream().map(this::toDto).toList();
    }

    @Transactional
    public void delete(Long workerUserId, Long id) {
        Portfolio portfolio = portfolioRepository.findByIdAndWorkerUserId(id, workerUserId)
                .orElseThrow(() -> new ApiException("Portfolio item not found", HttpStatus.NOT_FOUND));
        mediaUploadService.deleteImage(portfolio.getPublicId());
        portfolioRepository.delete(portfolio);
    }

    private PortfolioDto toDto(Portfolio portfolio) {
        return new PortfolioDto(portfolio.getId(), portfolio.getImageUrl(), portfolio.getTitle(), portfolio.getDescription(), portfolio.getCreatedAt());
    }
}
