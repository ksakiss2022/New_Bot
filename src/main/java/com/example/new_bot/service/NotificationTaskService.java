package com.example.new_bot.service;

import com.example.new_bot.entity.NotificationTask;
import com.example.new_bot.repository.NotificationTaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationTaskService {
    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Transactional
    public void addNotificationTask(LocalDateTime localDateTime, String massage, Long userId) {
        NotificationTask notificationTask = new NotificationTask();
        notificationTask.setNotificationDateTime(localDateTime);
        notificationTask.setMassage(massage);
        notificationTask.setUserId(userId);
        notificationTaskRepository.save(notificationTask);
    }

    public List<NotificationTask> notificationsForSend() {
        return notificationTaskRepository.findNotificationTasksByNotificationDateTime(LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES));
    }

    @Transactional
    public void deleteTask(NotificationTask notificationTask) {
        notificationTaskRepository.delete(notificationTask);
    }

    public void addNotificationTask(NotificationTask notificationTask) {
    }
}
