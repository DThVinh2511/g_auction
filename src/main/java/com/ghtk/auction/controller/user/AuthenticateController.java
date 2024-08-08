package com.ghtk.auction.controller.user;

import com.ghtk.auction.dto.request.user.AuthenticationRequest;
import com.ghtk.auction.dto.request.user.IntrospectRequest;
import com.ghtk.auction.dto.request.user.LogoutRequest;
import com.ghtk.auction.dto.request.user.RefreshRequest;
import com.ghtk.auction.dto.response.ApiResponse;
import com.ghtk.auction.dto.response.user.AuthenticationResponse;
import com.ghtk.auction.dto.response.user.IntrospectResponse;
import com.ghtk.auction.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("v1/auths")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class AuthenticateController {
    AuthenticationService authenticationService;

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(ApiResponse.success(authenticationService.authenticate(request)));
    }

    @PostMapping("/introspect")
    public ResponseEntity<ApiResponse<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {

        return ResponseEntity.ok(ApiResponse.success(authenticationService.introspect(request)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        return ResponseEntity.ok(ApiResponse.success(authenticationService.refreshToken(request)));
    }

    @PostMapping("/logout")
    ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ResponseEntity.ok(ApiResponse.success("logout successfully"));
    }
}
