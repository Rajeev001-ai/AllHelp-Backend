package com.project.AllHelp.service;

import com.project.AllHelp.dto.ActivityDto;
import com.project.AllHelp.entity.Activity;
import com.project.AllHelp.entity.ServiceRequest;
import com.project.AllHelp.repository.ActivityRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Transactional
    public void record(ServiceRequest request, String action, String performedBy) {
        Activity activity = new Activity();
        activity.setRequest(request);
        activity.setAction(action);
        activity.setPerformedBy(performedBy);
        activityRepository.save(activity);
    }

    @Transactional(readOnly = true)
    public List<ActivityDto> getRequestActivities(Long requestId) {
        return activityRepository.findByRequestIdOrderByCreatedAtAsc(requestId).stream()
                .map(activity -> new ActivityDto(activity.getId(), activity.getAction(), activity.getPerformedBy(), activity.getCreatedAt()))
                .toList();
    }
}
