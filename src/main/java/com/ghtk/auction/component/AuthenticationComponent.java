package com.ghtk.auction.component;

import com.ghtk.auction.dto.request.user.IntrospectRequest;
import com.ghtk.auction.dto.response.user.IntrospectResponse;
import com.ghtk.auction.entity.User;
import com.ghtk.auction.enums.UserStatus;
import com.ghtk.auction.exception.AuthenticatedException;
import com.ghtk.auction.repository.BlackListTokenRepository;
import com.ghtk.auction.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.Optional;

@Component
public class AuthenticationComponent {
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    @Autowired
    BlackListTokenRepository blackListTokenRepository;

    @Autowired
    UserRepository userRepository;



    public IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token);
        }
        catch (AuthenticatedException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    public SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) {
            throw new AuthenticatedException("Unauthenticated");
        }

        if (blackListTokenRepository.existsByToken(token))
            throw new AuthenticatedException("Unauthenticated");

        return signedJWT;
    }

    public boolean isUserValid(Jwt principal) {
        Long userId = (Long)principal.getClaim("id");
        if (userId == null) {
          return false;
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
          return false;
        }
        User u = user.get();
        return u.getIsVerified() && u.getStatusAccount() == UserStatus.ACTIVE;
    }
}
