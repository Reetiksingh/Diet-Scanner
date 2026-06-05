package com.nutrilens.common.security;

import java.util.UUID;

public record NutriLensPrincipal(UUID userId, String email) {
}

