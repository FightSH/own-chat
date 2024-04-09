package com.shen.chatgpt.application;

public interface IWeiXinValidateService {
    boolean checkSign(String signature, String timestamp, String nonce);
}
