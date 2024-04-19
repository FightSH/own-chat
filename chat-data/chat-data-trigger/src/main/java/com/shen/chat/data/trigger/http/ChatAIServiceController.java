package com.shen.chat.data.trigger.http;

import com.alibaba.fastjson2.JSON;
import com.shen.chat.data.domain.auth.service.IAuthService;
import com.shen.chat.data.domain.chatai.model.aggregates.ChatProcessAggregate;
import com.shen.chat.data.domain.chatai.model.entity.MessageEntity;
import com.shen.chat.data.domain.chatai.service.IChatService;
import com.shen.chat.data.trigger.http.dto.ChatRequestDTO;
import com.shen.chat.data.types.common.Constants;
import com.shen.chat.data.types.exception.ChatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/")
public class ChatAIServiceController {
    @Resource
    private IChatService chatService;
    @Resource
    private IAuthService authService;

    @PostConstruct
    public void init() {
        log.info("ChatAIServiceController init");
    }


    @PostMapping(value = "chat/completions")
    public ResponseBodyEmitter completionsStream(@RequestBody ChatRequestDTO request, @RequestHeader("Authorization") String token, HttpServletResponse response) {
        log.info("流式问答请求开始，使用模型：{} 请求信息：{}", request.getModel(), JSON.toJSONString(request.getMessages()));
        try {
            // 1. 基础配置；流式输出、编码、禁用缓存
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");

            // 2. 构建异步响应对象【对 Token 过期拦截】
            ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
            boolean success = authService.checkToken(token);

            if (!success) {
                try {
                    emitter.send(Constants.ResponseCode.TOKEN_ERROR.getCode());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                emitter.complete();
                return emitter;
            }



            // 2. 构建参数
            ChatProcessAggregate chatProcessAggregate = ChatProcessAggregate.builder()
                    .openId(token)
                    .model(request.getModel())
                    .messages(request.getMessages().stream()
                            .map(entity -> MessageEntity.builder()
                                    .role(entity.getRole())
                                    .content(entity.getContent())
                                    .name(entity.getName())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            // 3. 请求结果&返回
            return chatService.completions(emitter,chatProcessAggregate);
        } catch (Exception e) {
            log.error("流式应答，请求模型：{} 发生异常 {}", request.getModel(), e);
            throw new ChatException(e.getMessage());
        }
    }


    @PostMapping(value = "chat/test")
    public ResponseBodyEmitter completionsStream(HttpServletResponse response) {
        ResponseBodyEmitter responseBodyEmitter = new ResponseBodyEmitter();
        responseBodyEmitter.onCompletion(() -> {
            log.info("responseBodyEmitter请求完成");
        });
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                responseBodyEmitter.send("demo");
                Thread.sleep(3000L);
                responseBodyEmitter.send("test");
                responseBodyEmitter.complete();
            } catch (Exception ignore) {
            }
        });

        return responseBodyEmitter;
    }

}
