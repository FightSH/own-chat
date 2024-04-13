package com.shen.chat.data.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatModel {
    /**
     * gpt-3.5-turbo
     */
    GPT_3_5_TURBO("gpt-3.5-turbo"),
    ChatGLM("ChatGLM");
    private final String code;
}
