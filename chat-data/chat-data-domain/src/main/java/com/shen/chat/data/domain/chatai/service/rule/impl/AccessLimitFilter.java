package com.shen.chat.data.domain.chatai.service.rule.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.shen.chat.data.domain.chatai.model.aggregates.ChatProcessAggregate;
import com.shen.chat.data.domain.chatai.model.entity.RuleLogicEntity;
import com.shen.chat.data.domain.chatai.service.rule.ILogicFilter;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;

public class AccessLimitFilter implements ILogicFilter {


    @Value("${app.config.limit-count:10}")
    private Integer limitCount;
    @Value("${app.config.white-list}")
    private String whiteListStr;
    @Resource
    private Cache<String, Integer> visitCache;

    @Override
    public RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcess) throws Exception {
        return null;
    }
}
