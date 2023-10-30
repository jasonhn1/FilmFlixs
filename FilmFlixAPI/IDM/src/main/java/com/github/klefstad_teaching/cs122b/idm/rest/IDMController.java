package com.github.klefstad_teaching.cs122b.idm.rest;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BasicResults;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.core.result.Result;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.idm.component.IDMAuthenticationManager;
import com.github.klefstad_teaching.cs122b.idm.component.IDMJwtManager;
import com.github.klefstad_teaching.cs122b.idm.config.IDMServiceConfig;
import com.github.klefstad_teaching.cs122b.idm.data.AccessTokenRequestModel;
import com.github.klefstad_teaching.cs122b.idm.data.RefreshTokenRequestModel;
import com.github.klefstad_teaching.cs122b.idm.data.UserRequestModel;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.UserStatus;
import com.github.klefstad_teaching.cs122b.idm.response.ResultAuthenticate;
import com.github.klefstad_teaching.cs122b.idm.response.ResultLogin;
import com.github.klefstad_teaching.cs122b.idm.response.ResultRefresh;
import com.github.klefstad_teaching.cs122b.idm.response.ResultRegister;
import com.github.klefstad_teaching.cs122b.idm.util.Validate;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.*;

@RestController
public class IDMController
{
    private final IDMAuthenticationManager authManager;
    private final IDMJwtManager            jwtManager;
    private final Validate                 validate;


    @Autowired
    public IDMController(IDMAuthenticationManager authManager,
                         IDMJwtManager jwtManager,
                         Validate validate)
    {
        this.authManager = authManager;
        this.jwtManager = jwtManager;
        this.validate = validate;
    }

    /* throw new ResultError()
        this.template.queryForObject() <- only returns One object
        this.template.query() <- always returns list empty or nah [0-100]

     */

    // Because regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{10,20}$"; doesn't pass no upper case :)
    public Boolean isValidPassword(char[] password){
        Boolean lower = false;
        Boolean upper = false;
        Boolean number = false;

        for (char passletter: password){

            if(Character.isLowerCase(passletter)){
                lower = true;
            }else if (Character.isUpperCase(passletter)){
                upper = true;
            }else if (Character.isDigit(passletter)){
                number = true;
            }

        }

        return lower && upper && number;

    }

    @PostMapping("/register")
    public ResponseEntity<ResultRegister> register
            (@RequestBody UserRequestModel request) {

        String email = request.getEmail();
        char[] password = request.getPassword();

        // If the password's length is invalid
        if(!(password.length >= 10 && password.length <= 20)) {
            return new ResultRegister()
                    .setResult(IDMResults.PASSWORD_DOES_NOT_MEET_LENGTH_REQUIREMENTS)
                    .toResponse();
        }


        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{10,20}$";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password.toString());

        // If the password meets char requirements
        if(!isValidPassword(password)){
            return new ResultRegister()
                    .setResult(IDMResults.PASSWORD_DOES_NOT_MEET_CHARACTER_REQUIREMENT)
                    .toResponse();
        }


        regex = "^(.+)@(.+)$";


        p = Pattern.compile(regex);
        m = p.matcher(email);

        // If the email is invalid in format
        if(!m.matches()){
            return new ResultRegister()
                    .setResult(IDMResults.EMAIL_ADDRESS_HAS_INVALID_FORMAT)
                    .toResponse();
        }


        // If the email's length is invalid
        if(!(email.length() >= 6 && email.length() <=32)) {
            return new ResultRegister()
                    .setResult(IDMResults.EMAIL_ADDRESS_HAS_INVALID_LENGTH)
                    .toResponse();
        }


        // Now that the email and password are valid we want to regeister the user


      //  authManager.selectAndAuthenticateUser(email,password.toCharArray());

        //if the email is in use
        if(authManager.isEmailInUse(email)){


            return new ResultRegister()
                    .setResult(IDMResults.USER_ALREADY_EXISTS)
                    .toResponse();
        }else{// if email is not in use we want to create and insert a user into db

            authManager.createAndInsertUser(email,password);

            return new ResultRegister()
                    .setResult(IDMResults.USER_REGISTERED_SUCCESSFULLY)
                    .toResponse();
        }


    }


    @PostMapping("/login")
    public ResponseEntity<ResultLogin> login
            (@RequestBody UserRequestModel request) {

        // Validating User Errors when logging in

        String email = request.getEmail();
        char[] password = request.getPassword();

        // If the password's length is invalid
        if(!(password.length >= 10 && password.length <= 20)) {
            return new ResultLogin()
                    .setResult(IDMResults.PASSWORD_DOES_NOT_MEET_LENGTH_REQUIREMENTS)
                    .toResponse();
        }

        // If the password meets char requirements
        if(!isValidPassword(password)){
            return new ResultLogin()
                    .setResult(IDMResults.PASSWORD_DOES_NOT_MEET_CHARACTER_REQUIREMENT)
                    .toResponse();
        }



        String regex = "^(.+)@(.+)$";


        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);

        // If the email is invalid in format
        if(!m.matches()){
            return new ResultLogin()
                    .setResult(IDMResults.EMAIL_ADDRESS_HAS_INVALID_FORMAT)
                    .toResponse();
        }


        // If the email's length is invalid
        if(!(email.length() >= 6 && email.length() <=32)) {
            return new ResultLogin()
                    .setResult(IDMResults.EMAIL_ADDRESS_HAS_INVALID_LENGTH)
                    .toResponse();
        }


        // User not found
        if(!authManager.isEmailInUse(email)){
            return new ResultLogin()
                    .setResult(IDMResults.USER_NOT_FOUND)
                    .toResponse();
        }

        User logged_user = authManager.selectAndAuthenticateUser(email,password);

        // Passwords do not match
        if (logged_user == null){
            return new ResultLogin()
                    .setResult(IDMResults.INVALID_CREDENTIALS)
                    .toResponse();
        }else{
            // if passwords do match

            // If user is locked
            if(logged_user.getUserStatus() == UserStatus.LOCKED){
                return new ResultLogin()
                        .setResult(IDMResults.USER_IS_LOCKED)
                        .toResponse();
            }

            // If user is banned
            if(logged_user.getUserStatus() == UserStatus.BANNED){
                return new ResultLogin()
                        .setResult(IDMResults.USER_IS_BANNED)
                        .toResponse();
            }

            // return access and refresh token

            //Access Token
            String accessToken = jwtManager.buildAccessToken(logged_user);

            // Refresh token
            RefreshToken refreshToken = jwtManager.buildRefreshToken(logged_user);

            // Insert Refresh token to DB
            authManager.insertRefreshToken(refreshToken);

            ResultLogin response =  new ResultLogin();
            response.setResult(IDMResults.USER_LOGGED_IN_SUCCESSFULLY);
            response.setRefreshToken(refreshToken.getToken());
            response.setAccessToken(accessToken);


            return response.toResponse();
        }

    }




    @PostMapping("/refresh")
    public ResponseEntity<ResultRefresh> login
            (@RequestBody RefreshTokenRequestModel requestToken) {

        String request = requestToken.getRefreshToken();

        // All refreshTokens are 36 characters long
        if(request.length() != 36) {
            return new ResultRefresh()
                    .setResult(IDMResults.REFRESH_TOKEN_HAS_INVALID_LENGTH)
                    .toResponse();
        }

        // All refreshTokens must be in UUID form

        String regex = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(request);

        // If the request is in UUID form
        if(!m.matches()){
            return new ResultRefresh()
                    .setResult(IDMResults.REFRESH_TOKEN_HAS_INVALID_FORMAT)
                    .toResponse();
        }

        // If no refreshToken is found it will throw an error
        RefreshToken refreshToken = authManager.verifyRefreshToken(request);

        // If the refreshToken is expired
        if(jwtManager.hasExpired(refreshToken)){

            // We want to update the tokenStatus in DB to expired
            authManager.expireRefreshToken(refreshToken);


        }else{// if its not expired we want to refresh

            //First increase the TokenExpire Time
            jwtManager.updateRefreshTokenExpireTime(refreshToken);

            //Then update the TokenExpire Time in DB
            authManager.updateRefreshTokenExpireTime(refreshToken);

            // Check if expire time as after maxTime
            if(jwtManager.needsRefresh(refreshToken)){

                authManager.revokeRefreshToken(refreshToken);
                User user = authManager.getUserFromRefreshToken(refreshToken);

                String accessToken = jwtManager
                        .buildAccessToken(user);

                RefreshToken newRefreshToken = jwtManager.buildRefreshToken(user);


                // Insert NEW Refresh token to DB
                authManager.insertRefreshToken(newRefreshToken);

                ResultRefresh response =  new ResultRefresh();
                response.setResult(IDMResults.RENEWED_FROM_REFRESH_TOKEN);
                response.setRefreshToken(newRefreshToken.getToken());
                response.setAccessToken(accessToken);

                return response.toResponse();

            }else{
                // Return same refreshToken
                String accessToken = jwtManager
                        .buildAccessToken(authManager.getUserFromRefreshToken(refreshToken));

                ResultRefresh response =  new ResultRefresh();
                response.setResult(IDMResults.RENEWED_FROM_REFRESH_TOKEN);
                response.setRefreshToken(refreshToken.getToken());
                response.setAccessToken(accessToken);

                return response.toResponse();

            }

        }

        // It will never reach this
        return new ResultRefresh().setResult(IDMResults.REFRESH_TOKEN_IS_EXPIRED)
                .toResponse();
    }










    @PostMapping("/authenticate")
    public ResponseEntity<ResultAuthenticate> authenticate
            (@RequestBody AccessTokenRequestModel requestToken) {

        String request = requestToken.getAccessToken();


        if(request == null ){
            return new ResultAuthenticate()
                    .setResult(IDMResults.ACCESS_TOKEN_IS_INVALID)
                    .toResponse();
        }


        jwtManager.verifyAccessToken(request);

        return new ResultAuthenticate().setResult(IDMResults.ACCESS_TOKEN_IS_VALID)
                .toResponse();
    }


}
