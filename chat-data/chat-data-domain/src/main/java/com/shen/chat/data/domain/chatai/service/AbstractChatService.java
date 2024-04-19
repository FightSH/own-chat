package com.shen.chat.data.domain.chatai.service;

import cn.bugstack.chatglm.session.OpenAiSession;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.shen.chat.data.domain.auth.service.IAuthService;
import com.shen.chat.data.domain.chatai.model.aggregates.ChatProcessAggregate;
import com.shen.chat.data.domain.chatai.model.entity.RuleLogicEntity;
import com.shen.chat.data.domain.chatai.model.valobj.LogicCheckTypeVO;
import com.shen.chat.data.domain.chatai.service.rule.factory.DefaultLogicFactory;
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
        try {
            // 1. 请求应答
            emitter.onCompletion(() -> {
                log.info("流式问答请求完成，使用模型：{}", chatProcess.getModel());
            });
            emitter.onError(throwable -> log.error("流式问答请求疫情，使用模型：{}", chatProcess.getModel(), throwable));

            // 2. 规则过滤
            RuleLogicEntity<ChatProcessAggregate> ruleLogicEntity = this.doCheckLogic(chatProcess,
                    DefaultLogicFactory.LogicModel.ACCESS_LIMIT.getCode(),
                    DefaultLogicFactory.LogicModel.SENSITIVE_WORD.getCode());
            if (!LogicCheckTypeVO.SUCCESS.equals(ruleLogicEntity.getType())) {
                emitter.send(ruleLogicEntity.getInfo());
                emitter.complete();
                return emitter;
            }

            // 3. 应答处理
            this.doMessageResponse(ruleLogicEntity.getData(), emitter);
        } catch (Exception e) {
            throw new ChatException(Constants.ResponseCode.UN_ERROR.getCode(), Constants.ResponseCode.UN_ERROR.getInfo());
        }
        // 3. 返回结果
        return emitter;
    }

    protected abstract void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter responseBodyEmitter) throws JsonProcessingException;
    protected abstract RuleLogicEntity<ChatProcessAggregate> doCheckLogic(ChatProcessAggregate chatProcess, String... logics) throws Exception;

}


// curl -X POST http://localhost:8090/api/v1/chat/completions -H 'Content-Type: application/json;charset=utf-8' -H 'Authorization: b8b6' -d '{"messages": [{"content": "写一个java冒泡排序","role": "user"}],"model": "gpt-3.5-turbo"}'