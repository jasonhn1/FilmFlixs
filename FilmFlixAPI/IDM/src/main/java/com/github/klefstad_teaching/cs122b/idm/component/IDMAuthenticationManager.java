package com.github.klefstad_teaching.cs122b.idm.component;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.config.IDMServiceConfig;
import com.github.klefstad_teaching.cs122b.idm.repo.IDMRepo;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Component
public class IDMAuthenticationManager
{
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String       HASH_FUNCTION = "PBKDF2WithHmacSHA512";

    private static final int ITERATIONS     = 10000;
    private static final int KEY_BIT_LENGTH = 512;

    private static final int SALT_BYTE_LENGTH = 4;

    private final IDMRepo repo;


    @Autowired
    public IDMAuthenticationManager(IDMRepo repo)
    {
        this.repo = repo;
    }

    private static byte[] hashPassword(final char[] password, String salt)
    {
        return hashPassword(password, Base64.getDecoder().decode(salt));
    }

    private static byte[] hashPassword(final char[] password, final byte[] salt)
    {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(HASH_FUNCTION);

            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_BIT_LENGTH);

            SecretKey key = skf.generateSecret(spec);

            return key.getEncoded();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] genSalt()
    {
        byte[] salt = new byte[SALT_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }

    public Boolean isEmailInUse(String email){

        List<User> users = repo.selectUser(email);

        if (users.isEmpty()){
            return false;
        }

        return true;
    }

    // This
    public User selectAndAuthenticateUser(String email, char[] password)
    {

        List<User> users = repo.selectUser(email);

        User user = users.get(0);
        if (user == null){
            return null;
        }
        // Check for if passwords are the same
        if (user.getHashedPassword().equals(Base64.getEncoder().encodeToString(hashPassword(password,user.getSalt())))){
            return user;
        }

        // If they are not the same return null
        return null;
    }

    // Given a valid email and password we want to salt the password and hash it then
    // store the email,salt and salted password into idm.user
    public void createAndInsertUser(String email, char[] password)
    {
        byte[] salt = genSalt();
        byte[] hashed_password = hashPassword(password,salt);

        String base64EncodedHashedPassword = Base64.getEncoder().encodeToString(hashed_password);
        String base64EncodedHashedSalt = Base64.getEncoder().encodeToString(salt);

        repo.insertUser(email,base64EncodedHashedSalt,base64EncodedHashedPassword);
    }

    public void insertRefreshToken(RefreshToken refreshToken)
    {
        repo.insertRefreshToken(refreshToken);

    }

    // Checks to see if given token is a valid RefreshToken in DB
    public RefreshToken verifyRefreshToken(String token)
    {

        // We select the refreshToken
        RefreshToken refreshToken = repo.selectRefreshToken(token);


        if (refreshToken != null){
            //Check to see if the tokenStatus is expired
            if(refreshToken.getTokenStatus() == TokenStatus.EXPIRED){
                throw new ResultError(IDMResults.REFRESH_TOKEN_IS_EXPIRED);
            }

            //Check to see if the tokenStatus is revovked
            if(refreshToken.getTokenStatus() == TokenStatus.REVOKED){
                throw new ResultError(IDMResults.REFRESH_TOKEN_IS_REVOKED);
            }

            return refreshToken;
        }else{ // If we did not find the token in DB
            throw new ResultError(IDMResults.REFRESH_TOKEN_NOT_FOUND);
        }

    }


    public void updateRefreshTokenExpireTime(RefreshToken token)
    {
        repo.updateRefreshTokenExpireTime(token);
    }

    public void expireRefreshToken(RefreshToken token)
    {
        repo.updateRefreshTokenStatus(token,TokenStatus.EXPIRED);
        throw new ResultError(IDMResults.REFRESH_TOKEN_IS_EXPIRED);
    }

    public void revokeRefreshToken(RefreshToken token)
    {
        repo.updateRefreshTokenStatus(token,TokenStatus.REVOKED);
    }

    public User getUserFromRefreshToken(RefreshToken refreshToken)
    {

        User user = repo.getUserFromRefreshToken(refreshToken);

        return user;
    }
}
