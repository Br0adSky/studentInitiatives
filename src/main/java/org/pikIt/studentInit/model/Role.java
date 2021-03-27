package org.pikIt.studentInit.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER, EXPERT, MODERATOR, SUPER_USER;

    @Override
    public String getAuthority() {
        return name();
    }
}
