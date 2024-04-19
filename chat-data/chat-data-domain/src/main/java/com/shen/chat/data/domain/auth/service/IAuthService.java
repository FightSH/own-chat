package com.shen.chat.data.domain.auth.service;

import com.shen.chat.data.domain.auth.model.entity.AuthStateEntity;

public interface IAuthService {
    AuthStateEntity doLogin(String code);

    boolean checkToken(String token);

}
