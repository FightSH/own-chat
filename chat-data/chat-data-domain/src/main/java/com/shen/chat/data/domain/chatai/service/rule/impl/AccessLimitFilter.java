package com.shen.chat.data.domain.chatai.service.rule.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.shen.chat.data.domain.chatai.model.aggregates.ChatProcessAggregate;
import com.shen.chat.data.domain.chatai.model.entity.RuleLogicEntity;
import com.shen.chat.data.domain.chatai.model.valobj.LogicCheckTypeVO;
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
        // 1. 白名单用户直接放行
        if (chatProcess.isWhiteList(whiteListStr)) {
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS).data(chatProcess).build();
        }
        String openid = chatProcess.getOpenId();

        // 2. 访问次数判断
        int visitCount = visitCache.get(openid, c -> 0);
        if (visitCount < limitCount) {
            visitCache.put(openid, visitCount + 1);
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS).data(chatProcess).build();
        }

        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .info("您今日的免费" + limitCount + "次，已耗尽！")
                .type(LogicCheckTypeVO.REFUSE).data(chatProcess).build();
    }
}
