package com.karoljanowski.domain.security;

import org.springframework.security.core.GrantedAuthority;

/**
 * Created by Karol Janowski on 2017-06-08.
 */
public class Authority implements GrantedAuthority {
    private final String authority;

    public Authority(String authority){
        this.authority=authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
