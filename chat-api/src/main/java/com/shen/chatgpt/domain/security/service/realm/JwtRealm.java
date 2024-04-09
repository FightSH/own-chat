package com.shen.chatgpt.domain.security.service.realm;

import com.shen.chatgpt.domain.security.service.JwtUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtRealm extends AuthorizingRealm {

    private final Logger logger = LoggerFactory.getLogger(JwtRealm.class);

    private static JwtUtil jwtUtil = new JwtUtil();

    @Override
    public boolean supports(AuthenticationToken token) {
        return super.supports(token);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        final String jwtToken = (String)authenticationToken.getPrincipal();
        if (jwtToken == null) {
            throw new NullPointerException("jwtToken 不能为空");
        }
        if (!jwtUtil.isVerify(jwtToken)) {
            throw new UnknownAccountException();
        }
        String username = (String) jwtUtil.decode(jwtToken).get("username");

        return new SimpleAuthenticationInfo(jwtToken, jwtToken, "JwtRealm");


    }
}
