package com.unitrack;

import com.unitrack.activity.Activity;
import com.unitrack.activity.ActivityRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class NotificationsController {

    private final ActivityRepository activityRepository;

    public NotificationsController(
            ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @GetMapping("/notifications")
    public String notifications(Model model) {

        List<Activity> notifications =
                activityRepository.findTop10ByOrderByCreatedAtDesc();

        model.addAttribute("notifications", notifications);
        model.addAttribute(
                "notificationCount",
                activityRepository.countByReadStatusFalse()
        );

        return "notifications";
    }

    @PostMapping("/notifications/read/{id}")
    public String markAsRead(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Activity activity = activityRepository.findById(id).orElse(null);

        if (activity != null && !activity.isReadStatus()) {
            activity.setReadStatus(true);
            activityRepository.save(activity);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Notification marked as read."
            );
        }

        return "redirect:/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllAsRead() {

        List<Activity> unreadActivities =
                activityRepository.findTop10ByReadStatusFalseOrderByCreatedAtDesc();

        for (Activity activity : unreadActivities) {
            activity.setReadStatus(true);
        }

        activityRepository.saveAll(unreadActivities);

        return "redirect:/notifications";
    }
}
