package com.shen.chat.data.domain.weixin.service;

public interface IWeiXinValidateService {

    boolean checkSign(String signature, String timestamp, String nonce);

}
