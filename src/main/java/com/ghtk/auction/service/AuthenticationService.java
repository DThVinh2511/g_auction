package com.ghtk.auction.service;

import com.ghtk.auction.dto.request.user.AuthenticationRequest;
import com.ghtk.auction.dto.request.user.IntrospectRequest;
import com.ghtk.auction.dto.request.user.LogoutRequest;
import com.ghtk.auction.dto.request.user.RefreshRequest;
import com.ghtk.auction.dto.response.user.AuthenticationResponse;
import com.ghtk.auction.dto.response.user.IntrospectResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {

    IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException;

    AuthenticationResponse authenticate(AuthenticationRequest request);

    void logout(LogoutRequest request) throws ParseException, JOSEException;

    AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;
}
