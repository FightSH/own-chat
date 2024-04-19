package com.shen.chat.data.domain.chatai.service;

import cn.bugstack.chatglm.session.OpenAiSession;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.shen.chat.data.domain.auth.service.IAuthService;
import com.shen.chat.data.domain.chatai.model.aggregates.ChatProcessAggregate;
import com.shen.chat.data.types.common.Constants;
import com.shen.chat.data.types.exception.ChatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;

@Slf4j
public abstract class AbstractChatService implements IChatService {

    @Resource
    protected OpenAiSession openAiSession;
    @Resource
    private IAuthService authService;

    @Override
    public ResponseBodyEmitter completions(ResponseBodyEmitter emitter, ChatProcessAggregate chatProcess) {
        // 1. 校验权限
        if (!"b8b6".equals(chatProcess.getToken())) {
            throw new ChatException(Constants.ResponseCode.TOKEN_ERROR.getCode(), Constants.ResponseCode.TOKEN_ERROR.getInfo());
        }


        emitter.onCompletion(() -> {
            log.info("流式问答请求完成，使用模型：{}", chatProcess.getModel());
        });

        emitter.onError(throwable -> log.error("流式问答请求异常，使用模型：{}", chatProcess.getModel(), throwable));

        // 3. 应答处理
        try {
            this.doMessageResponse(chatProcess, emitter);
        } catch (Exception e) {
            log.error("流式问答请求异常 {}",  e);
            throw new ChatException(Constants.ResponseCode.UN_ERROR.getCode(),e.getMessage());
        }

        // 4. 返回结果
        return emitter;
    }

    protected abstract void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter responseBodyEmitter) throws JsonProcessingException;

}


// curl -X POST http://localhost:8090/api/v1/chat/completions -H 'Content-Type: application/json;charset=utf-8' -H 'Authorization: b8b6' -d '{"messages": [{"content": "写一个java冒泡排序","role": "user"}],"model": "gpt-3.5-turbo"}'