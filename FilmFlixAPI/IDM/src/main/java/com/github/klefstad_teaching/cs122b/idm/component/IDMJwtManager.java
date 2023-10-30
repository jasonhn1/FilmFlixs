package com.github.klefstad_teaching.cs122b.idm.component;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.idm.config.IDMServiceConfig;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Component
public class IDMJwtManager
{
    private final JWTManager jwtManager;
    private final IDMServiceConfig         config;
    @Autowired
    public IDMJwtManager(IDMServiceConfig serviceConfig)
    {
        this.jwtManager =
            new JWTManager.Builder()
                .keyFileName(serviceConfig.keyFileName())
                .accessTokenExpire(serviceConfig.accessTokenExpire())
                .maxRefreshTokenLifeTime(serviceConfig.maxRefreshTokenLifeTime())
                .refreshTokenExpire(serviceConfig.refreshTokenExpire())
                .build();

        this.config = serviceConfig;
    }


    private SignedJWT buildAndSignJWT(JWTClaimsSet claimsSet)
        throws JOSEException
    {

        JWSHeader header =
                new JWSHeader.Builder(JWTManager.JWS_ALGORITHM)
                        .keyID(jwtManager.getEcKey().getKeyID())
                        .type(JWTManager.JWS_TYPE)
                        .build();

        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        signedJWT.sign(jwtManager.getSigner());

        return signedJWT;
    }

    private void verifyJWT(String jwt)
        throws JOSEException, BadJOSEException
    {

    }

    public String buildAccessToken(User user)
    {

        JWTClaimsSet claimsSet =
                new JWTClaimsSet.Builder()
                        .subject(user.getEmail())
                        .expirationTime(Date.from(Instant.now().plus(config.accessTokenExpire()))) // When JWT expires ( Now + Expiration time)
                        .claim(JWTManager.CLAIM_ID, user.getId())    // we set claims like values in a map
                        .claim(JWTManager.CLAIM_ROLES, user.getRoles())
                        .issueTime(Date.from(Instant.now())) // When the JWT was issued
                        .build();
        SignedJWT token;
        try{
           token = buildAndSignJWT(claimsSet);

        }catch (JOSEException d){
            return "";
        }

      return token.serialize();
    }

    public void verifyAccessToken(String jws)
    {

        try {
            // Rebuild the SignedJWT from the serialized String
            SignedJWT signedJWT = SignedJWT.parse(jws);

            signedJWT.verify(jwtManager.getVerifier());
            jwtManager.getJwtProcessor().process(signedJWT, null);

            // Do logic to check if expired manually
            Instant time = signedJWT.getJWTClaimsSet().getExpirationTime().toInstant();

            // This means that the access token is expired
            if(Instant.now().isAfter(time)){
                throw new ResultError(IDMResults.ACCESS_TOKEN_IS_EXPIRED);
            }


        } catch (ParseException|IllegalStateException | JOSEException | BadJOSEException e) {
            //LOG.error("This is not a real token, DO NOT TRUST");
            throw new ResultError(IDMResults.ACCESS_TOKEN_IS_INVALID);
            // If the verify function throws an error that we know the
            // token can not be trusted and the request should not be continued
        }

    }


    public RefreshToken buildRefreshToken(User user)
    {
        RefreshToken refreshToken = new RefreshToken()
                .setTokenStatus(TokenStatus.ACTIVE)
                .setUserId(user.getId())
                .setToken(UUID.randomUUID().toString())
                .setExpireTime(Instant.now().plus(config.refreshTokenExpire()))
                .setMaxLifeTime(Instant.now().plus(config.maxRefreshTokenLifeTime()));

        return refreshToken;
    }

    // Checks to see if the refreshToken is expired compare to the current time
    public boolean hasExpired(RefreshToken refreshToken)
    {
        Instant time = Instant.now();
        Instant expireToken = refreshToken.getExpireTime();
        Instant maxLifeToken = refreshToken.getMaxLifeTime();


        // If the time is after the expire token or maxToken life then return true
        if (time.isAfter(expireToken) || time.isAfter(maxLifeToken)  ){
            return true;
        }
        return false;
    }

    public boolean needsRefresh(RefreshToken refreshToken)
    {
        // If the expireTime is after than the maxLife
        if(refreshToken.getExpireTime().isAfter(refreshToken.getMaxLifeTime())){
            return true;
        }

        return false;
    }

    public void updateRefreshTokenExpireTime(RefreshToken refreshToken)
    {
        refreshToken.setExpireTime(refreshToken.getExpireTime().plus(config.refreshTokenExpire()));

    }

    private UUID generateUUID()
    {
        return UUID.randomUUID();
    }
}
