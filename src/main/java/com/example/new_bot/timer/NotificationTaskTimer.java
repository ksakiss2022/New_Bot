package com.example.new_bot.timer;

import com.example.new_bot.service.NotificationTaskService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationTaskTimer {
    private final NotificationTaskService notificationTaskService;

    private final TelegramBot telegramBot;

    public NotificationTaskTimer(NotificationTaskService notificationTaskService, TelegramBot telegramBot) {
        this.notificationTaskService = notificationTaskService;

        this.telegramBot = telegramBot;
    }

    @Scheduled(fixedDelay = 60 * 1_000)
    public void checkNotifications() {
        notificationTaskService.notificationsForSend().forEach(notificationTask -> {
            telegramBot.execute(

                    new SendMessage(notificationTask.getUserId(),
                            "Вы просили напомнить об этом:" + notificationTask.getMassage())
            );
            notificationTaskService.deleteTask(notificationTask);
        });
    }
}
