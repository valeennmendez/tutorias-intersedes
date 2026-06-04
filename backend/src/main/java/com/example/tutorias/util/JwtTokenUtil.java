package com.example.tutorias.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.annotation.PostConstruct;


@Component
public class JwtTokenUtil {

     private static final long EXPIRATION_TIME = 10L * 24 * 60 * 60 * 1000;
     private Algorithm algorithm;


    @Value("${jwt.secret-key}")
     private String secretKey;

    @PostConstruct
    private void init(){
        this.algorithm = Algorithm.HMAC512(secretKey);
    }

      public String generateToken(String subject){
        
        String token = JWT.create()
            .withSubject(subject)
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(algorithm);

        return "Bearer " + token;
    }

    public boolean verify(String token){
        try {
            String cleanToken = token.replace("Bearer ", "");

            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(cleanToken);
            return true;

        } catch (JWTVerificationException e){   
            return false;
        }
    }


    public String getSubject(String token) {
        try {
            String cleanToken = token.replace("Bearer ", "");
            DecodedJWT decoded = JWT.require(algorithm).build().verify(cleanToken);
            return decoded.getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

}
