package com.example.new_bot.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.new_bot.model.NotificationTask;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {
    List<NotificationTask> findNotificationTasksByNotificationDateTime(LocalDateTime localDateTime);
}
