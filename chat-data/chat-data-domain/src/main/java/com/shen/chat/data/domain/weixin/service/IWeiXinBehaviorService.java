package com.shen.chat.data.domain.weixin.service;

import com.shen.chat.data.domain.weixin.model.entity.UserBehaviorMessageEntity;

public interface IWeiXinBehaviorService {

    String acceptUserBehavior(UserBehaviorMessageEntity userBehaviorMessageEntity);

}
