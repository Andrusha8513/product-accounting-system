package com.example.profile_service.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.io.Decoders;

import javax.crypto.SecretKey;
import java.security.PublicKey;

@Component
public class JwtService {

    private static final Logger log = LogManager.getLogger(JwtService.class);

    @Value("58cc2c665709725f73f283b76c4a4d277c038dc8a00d09209672ed631ddda7b2")
    private String jwtSecret;

    public String getEmailFromToken(String token){
        Claims  claims  = Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    private SecretKey getSingInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSingInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException exp) {
            log.error("Ошибка Expired JwtException ", exp);
        } catch (UnsupportedJwtException unx) {
            log.error("Ошибка Unsupported JwtException {} ", String.valueOf(unx));
        } catch (MalformedJwtException mnx) {
            log.error("Ошибка Malformed JwtException {} ", String.valueOf(mnx));
        } catch (SecurityException scp) {
            log.error("Ошибка Security Exception {}", String.valueOf(scp));
        } catch (Exception e) {
            log.error("Ошибка Exception {}", String.valueOf(e));
        }
        return false;
    }
}
