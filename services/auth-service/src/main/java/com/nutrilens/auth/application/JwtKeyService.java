package com.nutrilens.auth.application;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class JwtKeyService {
    private final RSAKey rsaKey;

    public JwtKeyService() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keyPair = generator.generateKeyPair();
            this.rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                    .privateKey((RSAPrivateKey) keyPair.getPrivate())
                    .keyID(UUID.randomUUID().toString())
                    .build();
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to create JWT signing key", exception);
        }
    }

    public RSAKey rsaKey() {
        return rsaKey;
    }

    public Map<String, Object> publicJwkSet() {
        return new JWKSet(rsaKey.toPublicJWK()).toJSONObject();
    }
}

