//package com.example.new_bot.model;
//
//import com.pengrad.telegrambot.Callback;
//import com.pengrad.telegrambot.impl.FileApi;
//import com.pengrad.telegrambot.impl.UpdatesHandler;
//import com.pengrad.telegrambot.model.File;
//import com.pengrad.telegrambot.request.BaseRequest;
//import com.pengrad.telegrambot.response.BaseResponse;
//import java.io.Serializable;
//
//
//public class TelegramBot implements Serializable {
//    //implements Serializable
//    TelegramBot bot = new TelegramBot("BOT_TOKEN");
//    private String token;
//    private TelegramBotClient api;
//    private FileApi fileApi;
//    private UpdatesHandler updatesHandler;
//
////    public TelegramBot(String token) {
////        this.token = token;
////        this.api = api;
////        this.fileApi = fileApi;
////        this.updatesHandler = updatesHandler;
////    }
////    public TelegramBot(String botToken) {
////        this(new Builder(botToken));
////    }
//
//    public TelegramBot(String botToken) {
//        this.token = botToken;
//        this.api = api;
//        this.fileApi = fileApi;
//        this.updatesHandler = updatesHandler;
//
//    }
//
//    //        TelegramBot(Builder builder) {
////        this.token = builder.botToken;
////        this.api = builder.api;
////        this.fileApi = builder.fileApi;
////        this.updatesHandler = builder.updatesHandler;
////    }
//    public <T extends BaseRequest<T, R>, R extends BaseResponse> R execute(BaseRequest<T, R> request) {
//        return api.send(request);
//    }
//
//    public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(T request, Callback<T, R> callback) {
//        api.send(request, callback);
//    }
//
//    public String getToken() {
//        return token;
//    }
//
//    public String getFullFilePath(File file) {
//        return fileApi.getFullFilePath(file.filePath());
//    }
//
////    public byte[] getFileContent(File file) throws IOException {
////        String fileUrl = getFullFilePath(file);
////        URLConnection connection = new URL(fileUrl).openConnection();
////        try (InputStream is = connection.getInputStream()) {
////
////        }
////    }
//}
