package com.shen.chat.data.trigger.http;

import com.alibaba.fastjson2.JSON;
import com.shen.chat.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.shen.chat.data.domain.openai.model.entity.MessageEntity;
import com.shen.chat.data.domain.openai.service.IChatService;
import com.shen.chat.data.trigger.http.dto.ChatRequestDTO;
import com.shen.chat.data.types.exception.ChatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

@Slf4j
@RestController()
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/")
public class ChatAIServiceController {
    @Resource
    private IChatService chatService;



    @PostMapping(value = "chat/completions")
    public ResponseBodyEmitter completionsStream(@RequestBody ChatRequestDTO request, @RequestHeader("Authorization") String token, HttpServletResponse response) {
        log.info("流式问答请求开始，使用模型：{} 请求信息：{}", request.getModel(), JSON.toJSONString(request.getMessages()));
        try {
            // 1. 基础配置；流式输出、编码、禁用缓存
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");

            // 2. 构建参数
            ChatProcessAggregate chatProcessAggregate = ChatProcessAggregate.builder()
                    .token(token)
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
            return chatService.completions(chatProcessAggregate);
        } catch (Exception e) {
            log.error("流式应答，请求模型：{} 发生异常", request.getModel(), e);
            throw new ChatException(e.getMessage());
        }
    }



}
