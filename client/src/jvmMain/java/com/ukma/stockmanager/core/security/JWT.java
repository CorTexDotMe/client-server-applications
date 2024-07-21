package com.ukma.stockmanager.core.security;

import io.jsonwebtoken.Jwts;

import java.security.Key;

public class JWT {
    private static final Key key = PacketCipher.generateKey();

    public static String createJWT(String value) {
        return Jwts.builder()
                .setSubject(value)
                .signWith(key)
                .compact();
    }

    public static String parseJWT(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }


}
