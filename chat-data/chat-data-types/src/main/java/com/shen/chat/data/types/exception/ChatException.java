package com.shen.chat.data.types.exception;

public class ChatException extends RuntimeException{
    /**
     * 异常码
     */
    private String code;

    /**
     * 异常信息
     */
    private String message;

    public ChatException(String code) {
        this.code = code;
    }

    public ChatException(String code, Throwable cause) {
        this.code = code;
        super.initCause(cause);
    }

    public ChatException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ChatException(String code, String message, Throwable cause) {
        this.code = code;
        this.message = message;
        super.initCause(cause);
    }
}
