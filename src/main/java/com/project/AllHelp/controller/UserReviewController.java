package com.project.AllHelp.controller;

import com.project.AllHelp.dto.CreateReviewDto;
import com.project.AllHelp.dto.ReviewResponseDto;
import com.project.AllHelp.security.UserPrincipal;
import com.project.AllHelp.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/reviews")
@PreAuthorize("hasRole('USER')")
public class UserReviewController {

    private final ReviewService reviewService;

    public UserReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody CreateReviewDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(principal.getId(), dto));
    }

    @GetMapping
    public List<ReviewResponseDto> getReviews(@AuthenticationPrincipal UserPrincipal principal) {
        return reviewService.getUserReviews(principal.getId());
    }
}
