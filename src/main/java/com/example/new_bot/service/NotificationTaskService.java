package com.example.new_bot.service;
import com.example.new_bot.model.NotificationTask;
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
    public NotificationTask save(NotificationTask init) {
      //  log.info("Requesting to save the task: " + init);
        return notificationTaskRepository.save(init);
    }

    @Transactional
    public void deleteTask(NotificationTask init) {
      //  log.debug("Requesting to delete the task:" + init);
        notificationTaskRepository.delete(init);
    }

    @Transactional
    public void addNotificationTask(LocalDateTime localDateTime, String massage, Long userId) {
        NotificationTask notificationTask = new NotificationTask();
        notificationTask.setNotificationDateTime(localDateTime);
        notificationTask.setMessage(massage);
        notificationTask.setUserId(userId);
        notificationTaskRepository.save(notificationTask);
    }

    @Transactional
    public List<NotificationTask> notificationsForSend() {
        //   log.info("Requesting tasks on time: " + LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        return notificationTaskRepository
                .findNotificationTasksByNotificationDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }
}
