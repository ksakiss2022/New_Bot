
package com.example.new_bot.listener;

import com.example.new_bot.model.NotificationTask;
import com.example.new_bot.service.NotificationTaskService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;

@Log4j
@Controller
public class TelegramBotUpdatesListener implements UpdatesListener {
    private static final Pattern NOTIFICATION_TASK_PATTERN = Pattern.compile(
            "([\\d\\\\.:\\s]{16})(\\s)([]А-яA-z\\s\\d,.!?;]+)");
       private final TelegramBot telegramBot;
    private final NotificationTaskService notificationTaskService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskService notificationTaskService) {
        this.telegramBot = telegramBot;
        this.notificationTaskService = notificationTaskService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(this::accept);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    void accept(Update update) {
        log.debug("Processing update: " + update);
        String message = update.message().text();
        if (update.message().photo() != null
                || update.message().sticker() != null
                || update.message().video() != null
                || update.message().audio() != null) {
            SendMessage errorMessage = new SendMessage(update.message().chat().id(),
                    "Запрос должен быть текстовым.");
            telegramBot.execute(errorMessage);
            return;
        }

        Matcher matcher = NOTIFICATION_TASK_PATTERN.matcher(message);
        if (matcher.matches()) {
            String date = matcher.group(1);
            String item = matcher.group(3);
            final LocalDateTime parseDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            NotificationTask task = new NotificationTask();
            task.setId(update.message().messageId());
   //         task.setUserId(update.message().chat().id());
            task.setMessage(item);
            task.setNotificationDateTime(parseDate);
            task.setUserId(update.message().from().id());
            notificationTaskService.save(task);
            SendMessage confirmMessage = new SendMessage(task.getUserId(),
                    "Задапа запланирована:\n" + task.getMessage()
                            + "\n на дату: \n" + task.getNotificationDateTime());
            telegramBot.execute(confirmMessage);
        } else {
            SendMessage errorMessage = new SendMessage(update.message().chat().id(),
                    "Формат сообщения должен быть следующим: " +
                            "[дата в формате dd.MM.yyyy HH:mm] [текст задачи]");
            telegramBot.execute(errorMessage);
        }
    }
}
