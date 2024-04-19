package com.shen.chat.data;

import cn.bugstack.chatglm.model.ChatCompletionRequest;
import cn.bugstack.chatglm.model.ChatCompletionResponse;
import cn.bugstack.chatglm.model.Model;
import cn.bugstack.chatglm.model.Role;
import cn.bugstack.chatglm.session.Configuration;
import cn.bugstack.chatglm.session.OpenAiSession;
import cn.bugstack.chatglm.session.OpenAiSessionFactory;
import cn.bugstack.chatglm.session.defaults.DefaultOpenAiSessionFactory;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.shen.chat.data.domain.chatai.model.aggregates.ChatProcessAggregate;
import com.shen.chat.data.domain.chatai.model.entity.MessageEntity;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
public class ApplicationTest {

    private OpenAiSession openAiSession;


    @Before
    public void test_OpenAiSessionFactory() {
        // 1. 配置文件
        Configuration configuration = new Configuration();
        configuration.setApiHost("https://open.bigmodel.cn/");
        configuration.setApiSecretKey("3367b6c5a3ad37e0928d3c10e03629ab.RwMSWKJqlfIczfQk");
//        configuration.setLevel(HttpLoggingInterceptor.Level.BODY);
        // 2. 会话工厂
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);
        // 3. 开启会话
        this.openAiSession = factory.openSession();
    }
// {"id":"8575298786674585913","created":1713282526,"model":"glm-4","choices":[{"index":0,"delta":{"role":"assistant","content":"我是一个"}}]}



    @Test
    public void testDemo() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 入参；模型、请求信息
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(Model.GLM_4); // GLM_3_5_TURBO、GLM_4
        request.setIsCompatible(false);
        // 24年1月发布的 glm-3-turbo、glm-4 支持函数、知识库、联网功能
//        request.setTools(new ArrayList<ChatCompletionRequest.Tool>() {
//            private static final long serialVersionUID = -7988151926241837899L;
//
//            {
//                add(ChatCompletionRequest.Tool.builder()
//                        .type(ChatCompletionRequest.Tool.Type.web_search)
//                        .webSearch(ChatCompletionRequest.Tool.WebSearch.builder().enable(true).searchQuery("小傅哥").build())
//                        .build());
//            }
//        });
        request.setMessages(new ArrayList<ChatCompletionRequest.Prompt>() {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content("你是谁")
                        .build());
            }
        });
        log.info("请求参数：{}", JSON.toJSONString(request));
        // 请求
        openAiSession.completions(request, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                if ("[DONE]".equals(data)) {
                    log.info("[输出结束] Tokens {}", com.alibaba.fastjson.JSON.toJSONString(data));
                    return;
                }

                ChatCompletionResponse response = com.alibaba.fastjson.JSON.parseObject(data, ChatCompletionResponse.class);
                log.info("测试结果：{}", com.alibaba.fastjson.JSON.toJSONString(response));
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

        // 等待
        countDownLatch.await();
    }

    @Test
    public void test() throws JsonProcessingException, InterruptedException {

        // 2. 请求应答
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
        final ChatProcessAggregate chatProcess = new ChatProcessAggregate();
        final MessageEntity messageEntity = new MessageEntity("user", "你是谁", "");
        chatProcess.setMessages(List.of(messageEntity));
        emitter.onCompletion(() -> {
            log.info("流式问答请求完成，使用模型：{}", chatProcess.getModel());
        });

        emitter.onError(throwable -> log.error("流式问答请求疫情，使用模型：{}", chatProcess.getModel(), throwable));

        // 3. 应答处理

        this.doMessageResponse(chatProcess, emitter);


//        emitter.wait();
    }


    protected void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter emitter) throws JsonProcessingException, InterruptedException {

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        // 1. 请求消息
        List<ChatCompletionRequest.Prompt> messages = chatProcess.getMessages().stream()
                .map(entity -> ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content(entity.getContent())
                        .build())
                .collect(Collectors.toList());
      //  {"incremental":false,"isCompatible":false,"messages":[{"content":"你是谁","role":"user"}],"model":"GLM_4","prompt":[{"content":"你是谁","role":"user"}],"stream":true,"temperature":0.0,"top_p":0.0}

        // 2. 封装参数

        // 入参；模型、请求信息
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(Model.GLM_4); // GLM_3_5_TURBO、GLM_4
        request.setIsCompatible(false);
        request.setStream(true);
        request.setMessages(messages);
//
        ChatCompletionRequest chatCompletion = ChatCompletionRequest
                .builder()
                .stream(true)
                .isCompatible(false)
                .messages(messages)
                .prompt(messages)
                .model(Model.GLM_4)
                .build();
        log.info("请求参数1：{}", JSON.toJSONString(request));
        log.info("请求参数2：{}", JSON.toJSONString(chatCompletion));
        // 3.2 请求应答
        try {
            openAiSession.completions(request, new EventSourceListener() {
                @Override
                public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {

                    ChatCompletionResponse chatCompletionResponse = JSON.parseObject(data, ChatCompletionResponse.class);
                    log.info("测试结果：{}", com.alibaba.fastjson.JSON.toJSONString(chatCompletionResponse));
//
                    List<ChatCompletionResponse.Choice> choices = chatCompletionResponse.getChoices();
                    for (ChatCompletionResponse.Choice chatChoice : choices) {
                        ChatCompletionResponse.Delta delta = chatChoice.getDelta();
                        if (Role.assistant.getCode().equals(delta.getRole())) continue;

                        // 应答完成
                        String finishReason = chatChoice.getFinishReason();
                        if (StringUtils.isNoneBlank(finishReason) && "stop".equals(finishReason)) {
                            log.info("测试结果：{}", com.alibaba.fastjson.JSON.toJSONString(chatCompletionResponse));
//                            Thread.sleep(5000L);
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
        countDownLatch.await();
    }
}
