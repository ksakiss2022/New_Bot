
package com.example.new_bot.listener;

import com.example.new_bot.entity.NotificationTask;
import com.example.new_bot.service.NotificationTaskService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.javapoet.ClassName;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import static java.time.LocalDateTime.parse;

@Component
public class TelegramBotUpdatesListener implements UpdatesListener {
    private static final Pattern NOTIFICATION_TASK_PATTERN = Pattern.compile(
            "([\\d\\\\.:\\s]{16})(\\s)([]А-яA-z\\s\\d,.!?;]+)");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final Logger LOG = (Logger) LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

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
            updates.forEach(update -> {
                LOG.info("Processing update:{}");
                String text = update.message().text();
                Long chatId = update.message().chat().id();
                if ("/start".equals(text)) {
                    SendMessage sendMessage = new SendMessage(chatId,
                            "Для планирования задачи отправьте её в формате:\n*01.01.2022 20:00 Сделать домашнюю работу*");
                    sendMessage.parseMode(ParseMode.Markdown);
                    telegramBot.execute(sendMessage);
                } else if (text != null) {
                    Matcher matcher = NOTIFICATION_TASK_PATTERN.matcher(text);
                    if (matcher.find()) {
                        LocalDateTime localDateTime = parse(matcher.group(1));
                        if (!Objects.isNull(localDateTime)) {
                            SendMessage sendMessage = new SendMessage(chatId, "Некорректный формат даты и /или времени");
                        } else {
                            String txt = matcher.group(2);
                            NotificationTask notificationTask = new NotificationTask();
                            notificationTask.setId(chatId);
                            notificationTask.setMessage(txt);
                            notificationTask.setNotificationDateTime(localDateTime);
                            notificationTaskService.addNotificationTask(notificationTask);
                            //   }
                            //} else {
                            //  telegramBot.execute(new SendMessage(chatId, "Некорректный формат даты и /или времени"));
                        }
                    } else {
                        SendMessage sendMessage = new SendMessage(chatId,
                                "Некорректный формат сообщения");
                    }
                }
            });
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}