package com.shen.chat.data.trigger.http;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/")
public class ChatAIServiceControllerOld {

//    @Resource
//    private OpenAiSession openAiSession;
//
//    @Resource
//    private ThreadPoolExecutor threadPoolExecutor;


//    /**
//     * 流式问题，ChatGPT 请求接口
//     * <p>
//     * curl -X POST \
//     * http://localhost:8080/api/v1/chat/completions \
//     * -H 'Content-Type: application/json;charset=utf-8' \
//     * -H 'Authorization: b8b6' \
//     * -d '{
//     * "messages": [
//     * {
//     * "content": "写一个java冒泡排序",
//     * "role": "user"
//     * }
//     * ],
//     * "model": "gpt-3.5-turbo"
//     * }'
//     */
//    @RequestMapping(value = "chat/completions", method = RequestMethod.POST)
//    public ResponseBodyEmitter completionsStream(@RequestBody ChatRequestDTO request, @RequestHeader("Authorization") String token, HttpServletResponse response) {
//        log.info("流式问答请求开始，使用模型：{} 请求信息：{}", request.getModel(), JSON.toJSONString(request.getMessages()));
//
//        try {
//            // 1. 基础配置；流式输出、编码、禁用缓存
//            response.setContentType("text/event-stream");
//            response.setCharacterEncoding("UTF-8");
//            response.setHeader("Cache-Control", "no-cache");
//
//            if (!token.equals("b8b6")) throw new RuntimeException("token err!");
//
//            // 2. 异步处理 HTTP 响应处理类
//            ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
//
//            emitter.onCompletion(() -> {
//                log.info("流式问答请求完成，使用模型：{}", request.getModel());
//            });
//            emitter.onError(throwable -> log.error("流式问答请求异常，使用模型：{}", request.getModel(), throwable));
//            // 3.1 构建参数
//            List<Message> messages = request.getMessages().stream()
//                    .map(entity -> Message.builder()
//                            .role(Constants.Role.valueOf(entity.getRole().toUpperCase()))
//                            .content(entity.getContent())
//                            .name(entity.getName())
//                            .build())
//                    .collect(Collectors.toList());
//
//            ChatCompletionRequest chatCompletion = ChatCompletionRequest
//                    .builder()
//                    .stream(true)
//                    .messages(messages)
//                    .model(Model.GLM_3_5_TURBO.getCode())
//                    .build();
//
//            // 3.2 请求应答
//            openAiSession.completions(chatCompletion, new EventSourceListener() {
//                @Override
//                public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
//                    ChatCompletionResponse chatCompletionResponse = JSON.parseObject(data, ChatCompletionResponse.class);
//                    List<ChatCompletionResponse.Choice> choices = chatCompletionResponse.getChoices();
//                    for (ChatCompletionResponse.Choice chatChoice : choices) {
//                        ChatCompletionResponse.Delta delta = chatChoice.getDelta();
//                        if (Role.assistant.getCode().equals(delta.getRole())) continue;
//
//                        // 应答完成
//                        String finishReason = chatChoice.getFinishReason();
//                        if (StringUtils.isNoneBlank(finishReason) && "stop".equals(finishReason)) {
//                            emitter.complete();
//                            break;
//                        }
//
//                        // 发送信息
//                        try {
//                            emitter.send(delta.getContent());
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//
//
//                }
//            });
//            return emitter;
//        } catch (Exception e) {
//            log.error("流式应答，请求模型：{} 发生异常", request.getModel(), e);
//            throw new ChatException(e.getMessage());
//        }
//    }
//
//    @RequestMapping(value = "/chat", method = RequestMethod.GET)
//    public ResponseBodyEmitter completionsStream(HttpServletResponse response) {
//        response.setContentType("text/event-stream");
//        response.setCharacterEncoding("UTF-8");
//        response.setHeader("Cache-Control", "no-cache");
//
//        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
//
//        threadPoolExecutor.execute(() -> {
//            for (int i = 0; i < 10; i++) {
//                try {
//                    emitter.send("strdddddddddddddddd\r\n" + i);
//                    Thread.sleep(100);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            emitter.complete();
//        });
//
//        return emitter;
//    }


}
