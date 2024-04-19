package com.shen.chat.data.domain.chatai.model.aggregates;

import com.shen.chat.data.domain.chatai.model.entity.MessageEntity;
import com.shen.chat.data.types.common.Constants;
import com.shen.chat.data.types.enums.ChatModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatProcessAggregate {

    /**
     * 验证信息
     */
    private String openId;
    /**
     * 默认模型
     */
    private String model = ChatModel.GLM_4.getCode();
    /**
     * 问题描述
     */
    private List<MessageEntity> messages;


    public boolean isWhiteList(String whiteListStr) {
        String[] whiteList = whiteListStr.split(Constants.SPLIT);
        for (String whiteOpenid : whiteList) {
            if (whiteOpenid.equals(openId)) return true;
        }
        return false;
    }



}