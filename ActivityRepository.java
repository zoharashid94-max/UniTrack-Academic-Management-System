package com.unitrack.activity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository
        extends JpaRepository<Activity, Long> {

    List<Activity> findTop10ByOrderByCreatedAtDesc();

    List<Activity> findTop10ByReadStatusFalseOrderByCreatedAtDesc();

    long countByReadStatusFalse();
}
