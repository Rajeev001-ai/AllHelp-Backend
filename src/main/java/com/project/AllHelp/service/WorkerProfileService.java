package com.project.AllHelp.service;

import com.project.AllHelp.dto.AdminUserSummaryDto;
import com.project.AllHelp.dto.UpdateWorkerAvailabilityDto;
import com.project.AllHelp.dto.UpdateWorkerProfileDto;
import com.project.AllHelp.dto.WorkerProfileDto;
import com.project.AllHelp.entity.AppUser;
import com.project.AllHelp.entity.Availability;
import com.project.AllHelp.entity.Role;
import com.project.AllHelp.entity.WorkerProfile;
import com.project.AllHelp.exception.ApiException;
import com.project.AllHelp.repository.ReviewRepository;
import com.project.AllHelp.repository.WorkerProfileRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class WorkerProfileService {

    private final WorkerProfileRepository workerProfileRepository;
    private final ReviewRepository reviewRepository;
    private final MediaUploadService mediaUploadService;

    public WorkerProfileService(WorkerProfileRepository workerProfileRepository, ReviewRepository reviewRepository, MediaUploadService mediaUploadService) {
        this.workerProfileRepository = workerProfileRepository;
        this.reviewRepository = reviewRepository;
        this.mediaUploadService = mediaUploadService;
    }

    @Transactional
    public WorkerProfile createDefaultProfile(AppUser user) {
        if (user.getRole() != Role.WORKER) {
            throw new ApiException("Only workers can have worker profiles", HttpStatus.BAD_REQUEST);
        }

        WorkerProfile profile = new WorkerProfile();
        profile.setUser(user);
        profile.setAvailability(Availability.OFFLINE);
        return workerProfileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public List<WorkerProfileDto> getWorkers(String city, Availability availability, Boolean verified, String skill) {
        return workerProfileRepository.findAll((root, query, criteriaBuilder) -> {
                    root.fetch("user");
                    query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
                    List<Predicate> predicates = new ArrayList<>();
                    if (city != null && !city.isBlank()) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), "%" + city.toLowerCase() + "%"));
                    }
                    if (availability != null) {
                        predicates.add(criteriaBuilder.equal(root.get("availability"), availability));
                    }
                    if (verified != null) {
                        predicates.add(criteriaBuilder.equal(root.get("verified"), verified));
                    }
                    if (skill != null && !skill.isBlank()) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("skills")), "%" + skill.toLowerCase() + "%"));
                    }
                    return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
                }).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkerProfileDto getWorker(Long id) {
        return workerProfileRepository.findWithUserById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ApiException("Worker not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public WorkerProfileDto verifyWorker(Long id) {
        WorkerProfile worker = workerProfileRepository.findWithUserById(id)
                .orElseThrow(() -> new ApiException("Worker not found", HttpStatus.NOT_FOUND));
        worker.setVerified(true);
        return toDto(workerProfileRepository.save(worker));
    }

    @Transactional
    public WorkerProfileDto updateAvailability(Long id, UpdateWorkerAvailabilityDto dto) {
        WorkerProfile worker = workerProfileRepository.findWithUserById(id)
                .orElseThrow(() -> new ApiException("Worker not found", HttpStatus.NOT_FOUND));
        worker.setAvailability(dto.availability());
        return toDto(workerProfileRepository.save(worker));
    }

    @Transactional(readOnly = true)
    public WorkerProfileDto getProfileForUser(Long userId) {
        return workerProfileRepository.findByUserId(userId)
                .map(this::toDto)
                .orElseThrow(() -> new ApiException("Worker profile not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public WorkerProfileDto updateProfileForUser(Long userId, UpdateWorkerProfileDto dto) {
        WorkerProfile worker = workerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException("Worker profile not found", HttpStatus.NOT_FOUND));
        if (dto.fullName() != null && !dto.fullName().isBlank()) {
            worker.getUser().setFullName(dto.fullName());
        }
        worker.setSkills(dto.skills());
        worker.setExperience(dto.experience() == null ? 0 : dto.experience());
        worker.setBio(dto.bio());
        worker.setCity(dto.city());
        if (dto.availability() != null) {
            worker.setAvailability(dto.availability());
        }
        return toDto(workerProfileRepository.save(worker));
    }

    @Transactional
    public WorkerProfileDto uploadProfileImage(Long userId, MultipartFile file) {
        WorkerProfile worker = workerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException("Worker profile not found", HttpStatus.NOT_FOUND));
        worker.getUser().setProfilePicture(mediaUploadService.uploadImage(file, "allhelp/profile").url());
        return toDto(workerProfileRepository.save(worker));
    }

    public WorkerProfileDto toDto(WorkerProfile worker) {
        AppUser user = worker.getUser();
        return new WorkerProfileDto(
                worker.getId(),
                new AdminUserSummaryDto(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(), user.getProfilePicture(), user.getAddress(), user.getCity()),
                worker.getSkills(),
                worker.getExperience(),
                worker.getBio(),
                worker.getCity(),
                worker.getAvailability(),
                worker.getVerified(),
                worker.getRating(),
                reviewRepository.countByWorkerId(worker.getId()),
                worker.getCompletedJobs(),
                worker.getCreatedAt()
        );
    }
}
