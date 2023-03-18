package com.example.new_bot.model;

import com.google.gson.Gson;
import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TelegramBotClient {
    private final OkHttpClient client;
    private OkHttpClient clientWithTimeout;
    private final Gson gson;
    private final String baseUrl;

    public TelegramBotClient(OkHttpClient client, Gson gson, String baseUrl) {
        this.client = client;
        this.clientWithTimeout = client;
        this.gson = gson;
        this.baseUrl = baseUrl;
    }

    public <T extends BaseRequest<T, R>, R extends BaseResponse> void send(final T request, final Callback<T, R> callback) {
        OkHttpClient client = getOkHttpClient(request);
        client.newCall(createRequest(request)).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                R result = null;
                Exception exception = null;
                try {
                    result = gson.fromJson(response.body().string(), request.getResponseType());
                } catch (Exception e) {
                    exception = e;
                }
                if (result != null) {
                    callback.onResponse(request, result);
                } else if (exception != null) {
                    IOException ioEx = exception instanceof IOException ? (IOException) exception :
                            new IOException(exception);
                    callback.onFailure(request, ioEx);
                } else {
                    callback.onFailure(request, new IOException("Empty response"));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(request, e);
            }
        });
    }

    public <T extends BaseRequest<T, R>, R extends BaseResponse> R send(final BaseRequest<T, R> request) {
        try {
            OkHttpClient client = getOkHttpClient(request);
            Response response = client.newCall(createRequest(request)).execute();
            return gson.fromJson(response.body().string(), request.getResponseType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        client.dispatcher().executorService().shutdown();
    }

    private OkHttpClient getOkHttpClient(BaseRequest<?, ?> request) {
        int timeoutMills = request.getTimeoutSeconds() + 1000;
        if (client.readTimeoutMillis() == 0 || client.readTimeoutMillis() > timeoutMills) return client;
        if (clientWithTimeout.readTimeoutMillis() > timeoutMills) return clientWithTimeout;

        clientWithTimeout = client.newBuilder().readTimeout(timeoutMills + 1000, TimeUnit.MILLISECONDS).build();
        return clientWithTimeout;
    }

    private Request createRequest(BaseRequest<?, ?> request) {
        return new Request.Builder()
                .url(baseUrl + request.getMethod())
                .post(createRequestBody(request))
                .build();
    }

    private RequestBody createRequestBody(BaseRequest<?, ?> request) {
        if (request.isMultipart()) {
            MediaType concentType = MediaType.parse(request.getContentType());

            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

            for (Map.Entry<String, Object> parameter : request.getParameters().entrySet()) {
                String name = parameter.getKey();
                Object value = parameter.getValue();
                if (value instanceof byte[]) {
                    builder.addFormDataPart(name, request.getFileName(), RequestBody.create(concentType, (byte[]) value));

                } else if (value instanceof File) {
                    builder.addFormDataPart(name, request.getFileName(), RequestBody.create(concentType, (File) value));
                } else {
                    builder.addFormDataPart(name, toParamValue(value));
                }
            }
            return builder.build();
        } else {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, Object> parameter : request.getParameters().entrySet()) {
                builder.add(parameter.getKey(), toParamValue(parameter.getValue()));
            }
            return builder.build();
        }
    }

    private String toParamValue(Object obj) {
        if (obj.getClass().isPrimitive() ||
                obj.getClass().isEnum() ||
                obj.getClass().getName().startsWith("java.lang")) {

        }
        return gson.toJson(obj);
    }

    public void execute(SendMessage sendMessage) {
    }
}
