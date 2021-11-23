/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.userinterface.uichassis.springboot.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.odpi.openmetadata.userinterface.uichassis.springboot.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

public class TokenService extends RoleService{

    @Value("${token.secret}")
    protected String tokenSecret;

    /**
     * token timout in minutes
     */
    @Value("${token.timeout:30}")
    protected Long tokenTimeout;

    /**
     *
     * @return token timeout in milliseconds
     */
    public long getTokenTimeout(){
        return tokenTimeout * 60 * 1000;
    }


    /**
     *
     * @param obj the object to be serialized
     * @return the json string representing TokenUser
     */
    public String toJSON(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     *
     * @param user the user to create token for
     * @param secret the secret for signature
     * @return jwt token
     */
    public String createTokenForUser(TokenUser user, String secret) {
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + getTokenTimeout()))
                .setSubject(toJSON(user))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     *
     * @param token the encoded token
     * @param secret secret phrase to decode
     * @return parsed TokenUser
     */
    public TokenUser parseUserFromToken(String token, String secret) {
        String userJSON = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return fromJSON(userJSON);
    }

    /**
     *
     * @param userJSON representation of the TokenUser
     * @return the TokenUser
     */
    public TokenUser fromJSON(final String userJSON) {
        try {
            return new ObjectMapper().readValue(userJSON, TokenUser.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
