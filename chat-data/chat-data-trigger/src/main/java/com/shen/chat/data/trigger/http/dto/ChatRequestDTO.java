package com.shen.chat.data.trigger.http.dto;

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
public class ChatRequestDTO {
    /** 默认模型 */
    private String model = ChatModel.GPT_3_5_TURBO.getCode();

    /** 问题描述 */
    private List<MessageEntity> messages;
}
