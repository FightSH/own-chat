package com.shen.chat.data.domain.openai.service;

import com.shen.chat.data.domain.openai.model.aggregates.ChatProcessAggregate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public interface IChatService {

    ResponseBodyEmitter completions(ChatProcessAggregate chatProcess);

}