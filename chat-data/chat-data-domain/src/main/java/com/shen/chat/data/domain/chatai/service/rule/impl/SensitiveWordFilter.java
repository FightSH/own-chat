package com.shen.chat.data.domain.chatai.service.rule.impl;

import com.shen.chat.data.domain.chatai.model.aggregates.ChatProcessAggregate;
import com.shen.chat.data.domain.chatai.model.entity.RuleLogicEntity;
import com.shen.chat.data.domain.chatai.service.rule.ILogicFilter;

public class SensitiveWordFilter implements ILogicFilter {
    @Override
    public RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcess) throws Exception {
        return null;
    }
}
