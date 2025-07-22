package com.bank.keycloakadapter.controller;

import com.auth0.jwk.Jwk;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bank.common.exception.BusinessException;
import com.bank.keycloakadapter.service.JwtService;
import com.bank.keycloakadapter.service.KeycloakRestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
public class IndexController {

    private final KeycloakRestService restService;

    private final JwtService jwtService;

    @GetMapping("/roles")
    public ResponseEntity<?> getRoles(@RequestHeader("Authorization") String authHeader)  {
        try {
            DecodedJWT jwt = JWT.decode(authHeader.replace("Bearer", "").trim());

            // check JWT is valid
            Jwk jwk = jwtService.getJwk();
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            algorithm.verify(jwt);

            // check JWT role is correct
            List<String> roles = ((List) jwt.getClaim("realm_access").asMap().get("roles"));

            // check JWT is still active
            Date expiryDate = jwt.getExpiresAt();
            if (expiryDate.before(new Date())) {
                throw new Exception("token is expired");
            }
            // all validation passed
            HashMap HashMap = new HashMap();
            for (String str : roles) {
                HashMap.put(str, str.length());
            }
            return ResponseEntity.ok(HashMap);
        } catch (Exception e) {
            log.error("exception : {} ", e.getMessage());
            throw new BusinessException("01", e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/valid")
    public ResponseEntity<?> valid(@RequestHeader("Authorization") String authHeader) {
        try {
            restService.checkValidity(authHeader);
            return ResponseEntity.ok(new HashMap (){{
                put("is_valid", "true");
            }});
        } catch (Exception e) {
            log.error("token is not valid, exception : {} ", e.getMessage());
            throw new BusinessException("is_valid", "False",HttpStatus.FORBIDDEN);

        }
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestParam("username") String username,@RequestParam("password") String password) {
        String login = restService.login(username, password);
        return ResponseEntity.ok(login);
    }

    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> logout(@RequestParam(value = "refresh_token", name = "refresh_token") String refreshToken)  {
        try {
            restService.logout(refreshToken);
            return ResponseEntity.ok(new HashMap (){{
                put("logout", "true");
            }});
        } catch (Exception e) {
            log.error("unable to logout, exception : {} ", e.getMessage());
            throw new BusinessException("logout", "False",HttpStatus.FORBIDDEN);
        }
    }
    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> refresh(@RequestParam(value = "refresh_token", name = "refresh_token") String refreshToken)  {
        try {
            return ResponseEntity.ok(restService.refresh(refreshToken));
        } catch (Exception e) {
            log.error("unable to refresh, exception : {} ", e.getMessage());
            throw new BusinessException("refresh", "False",HttpStatus.FORBIDDEN);
        }
    }
}
