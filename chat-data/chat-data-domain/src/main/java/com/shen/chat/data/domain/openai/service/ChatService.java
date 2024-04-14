package com.shen.chat.data.domain.openai.service;


import cn.bugstack.chatglm.model.ChatCompletionRequest;
import cn.bugstack.chatglm.model.ChatCompletionResponse;
import cn.bugstack.chatglm.model.Model;
import cn.bugstack.chatglm.model.Role;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.shen.chat.data.domain.openai.model.aggregates.ChatProcessAggregate;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.internal.http.RealResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
@Slf4j
@Service
public class ChatService extends AbstractChatService {

    @Override
    protected void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter emitter) throws JsonProcessingException {

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        // 1. 请求消息
        List<ChatCompletionRequest.Prompt> messages = chatProcess.getMessages().stream()
                .map(entity -> ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content(entity.getContent())
                        .build())
                .collect(Collectors.toList());

        // 2. 封装参数
        ChatCompletionRequest chatCompletion = ChatCompletionRequest
                .builder()
                .stream(true)
                .isCompatible(true)
                .messages(messages)
                .model(Model.GLM_4)
                .build();

        // 3.2 请求应答
        try {
            openAiSession.completions(chatCompletion, new EventSourceListener() {
                @Override
                public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                    ChatCompletionResponse chatCompletionResponse = JSON.parseObject(data, ChatCompletionResponse.class);
                    List<ChatCompletionResponse.Choice> choices = chatCompletionResponse.getChoices();
                    for (ChatCompletionResponse.Choice chatChoice : choices) {
                        ChatCompletionResponse.Delta delta = chatChoice.getDelta();
                        if (Role.assistant.getCode().equals(delta.getRole())) continue;

                        // 应答完成
                        String finishReason = chatChoice.getFinishReason();
                        if (StringUtils.isNoneBlank(finishReason) && "stop".equals(finishReason)) {
                            emitter.complete();
                            break;
                        }

                        // 发送信息
                        try {
                            emitter.send(delta.getContent());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
                @Override
                public void onClosed(EventSource eventSource) {
                    log.info("对话完成");
                    countDownLatch.countDown();
                }

                @Override
                public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                    log.error("对话失败", t);
                    countDownLatch.countDown();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
