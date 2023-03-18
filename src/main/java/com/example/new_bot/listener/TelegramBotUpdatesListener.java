package com.example.new_bot.listener;

import com.example.new_bot.entity.NotificationTask;
import com.example.new_bot.service.NotificationTaskService;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



@Component
public class TelegramBotUpdatesListener implements UpdatesListener {
    private static final Pattern NOTIFICATION_TASK_PATTERN = Pattern.compile(
            "([\\d\\\\.:\\s]{16})(\\s)([]А-яA-z\\s\\d,.!?;]+)");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final Logger LOG = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final TelegramBot telegramBot;
   private final NotificationTaskService notificationTaskService;

//    @Autowired
//   private  final TelegramBot telegramBot;


public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskService notificationTaskService){
    this.telegramBot=telegramBot;
    this.notificationTaskService = notificationTaskService;
}
    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

     private long lastProcessedMessageId = 0;
//
//    @Override
//    public int process(List<Update> updates) {
//        for (Update update : updates) {
//            if (update.message() != null && update.message().text() != null) {
//                bot.execute(new SendMessage(update.message().chat().id(), "Вы отправили сообщение: " + update.message().text()));
//                lastProcessedMessageId = update.message().messageId();
//            }
//        }
//        //return (int) lastProcessedMessageId;
//        return UpdatesListener.CONFIRMED_UPDATES_ALL;
//    }

    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(update -> {
                LOG.info("Processing update:{}", update);
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
                            SendMessage sendMessage = new SendMessage(chatId,"Некорректный формат даты и /или времени");
                        }else {
                            String txt = matcher.group(2);
                            NotificationTask notificationTask = new NotificationTask();
                            notificationTask.setId(chatId);
                            notificationTask.setMassage(txt);
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
          LOG.error(e.getMessage(),e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

//        private notificationTask pars(String s) {
//        LocalDateTime dateTime = LocalDateTime.parse(s, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
//        String massage = s.substring(s.lastIndexOf(":") + 3);
//        notificationTask example = new notificationTask();
//        example.setDateTime(dateTime);
//        example.setMassage(massage);
//        return example;
//    }
    @Nullable
    private LocalDateTime parse(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
