package com.ghtk.auction.config;

import com.ghtk.auction.component.AuthenticationComponent;
import com.ghtk.auction.dto.request.user.IntrospectRequest;
import com.ghtk.auction.exception.AuthenticatedException;
import com.ghtk.auction.exception.ExpiredTokenException;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")
    private String signerKey;

    @Autowired
    AuthenticationComponent authenticationComponent;
    @Autowired
    RedisTemplate<String, String> redisTemplate;


    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws ExpiredTokenException, JwtException  {

        try {
            var response = authenticationComponent.introspect(
                    IntrospectRequest.builder().token(token).build());

            if (!response.isValid() && response.getContent().equals("Unauthenticated"))
                throw new AuthenticatedException("Unauthenticated");
//            if(!response.isValid() && response.getContent().equals("ExpiredToken"))
//                throw new ExpiredTokenException("ExpiredToken");
            String tmp = redisTemplate.opsForValue().get("accessToken: " + response.getEmail());
            if(!tmp.equals(token)) {
                OAuth2Error oAuth2Error = new OAuth2Error("token_old", "JWT token has been deleted ", null);
                throw new OAuth2AuthenticationException(oAuth2Error);
            }
        } catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        } catch (ExpiredTokenException e) {
            OAuth2Error oAuth2Error = new OAuth2Error("token_expired", "JWT token has expired", null);
            throw new OAuth2AuthenticationException(oAuth2Error, e);
        }

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }
}
