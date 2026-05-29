package com.edumanageng.notification.repository;

import com.edumanageng.notification.entity.NotificationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    Page<NotificationLog> findBySchoolId(Long schoolId, Pageable pageable);
    Page<NotificationLog> findByRecipientUserId(Long userId, Pageable pageable);
}
