package com.nutrilens.common.security;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecuritySupport {
    private static final UUID LOCAL_DEVELOPMENT_USER = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private SecuritySupport() {
    }

    public static UUID currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof NutriLensPrincipal principal) {
            return principal.userId();
        }
        return LOCAL_DEVELOPMENT_USER;
    }
}

