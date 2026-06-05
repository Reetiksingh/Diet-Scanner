package com.nutrilens.auth.api;

import com.nutrilens.auth.application.JwtKeyService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwkController {
    private final JwtKeyService jwtKeyService;

    public JwkController(JwtKeyService jwtKeyService) {
        this.jwtKeyService = jwtKeyService;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> jwks() {
        return jwtKeyService.publicJwkSet();
    }
}

