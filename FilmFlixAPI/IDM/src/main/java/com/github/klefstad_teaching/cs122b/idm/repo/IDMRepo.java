package com.github.klefstad_teaching.cs122b.idm.repo;

import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class IDMRepo
{
    public final NamedParameterJdbcTemplate template;

    @Autowired
    public IDMRepo(NamedParameterJdbcTemplate template)
    {
        this.template = template;
    }

    public UserStatus getStatus(Integer status){

        if(status == 1){
            return UserStatus.ACTIVE;
        }else if (status == 2){
            return UserStatus.LOCKED;
        }else if(status ==3){
            return UserStatus.BANNED;
        }
        return null;
    };

    public TokenStatus getTokenStatus(Integer status){

        if(status == 1){
            return TokenStatus.ACTIVE;
        }else if (status == 2){
            return TokenStatus.EXPIRED;
        }else if(status ==3){
            return TokenStatus.REVOKED;
        }
        return null;
    };


    public List<User> selectUser(String email){


        String sql =
                "SELECT id, email, user_status_id, salt, hashed_password " +
                        "FROM idm.user " +
                        "WHERE email = :email;"; //notice we mark varaibles with the ':var' format


        MapSqlParameterSource source =
                new MapSqlParameterSource() //For ever ':var' we list a value and `Type` for value
                        .addValue("email", email, Types.VARCHAR); // Notice the lack of ':'  in the string here

        List<User> users =
                this.template.query(
                        sql,
                        source,
                        // For every row this lambda will be called to turn it into a Object (in this case `Student`)
                        (rs, rowNum) ->
                                new User()
                                        .setId(rs.getInt("id"))
                                        .setEmail(rs.getString("email"))
                                        .setUserStatus(getStatus(rs.getInt("user_status_id")))
                                        .setSalt(rs.getString("salt"))
                                        .setHashedPassword(rs.getString("hashed_password"))
                );

      return users;
    };

    public void insertUser(String email, String salt, String hashed_password){


        int rowsUpdated = this.template.update(
                "INSERT INTO idm.user (email, user_status_id, salt, hashed_password) " +
                        "VALUES (:email, :user_status_id, :salt, :hashed_password);",
                new MapSqlParameterSource()
                        .addValue("email", email, Types.VARCHAR)
                        .addValue("user_status_id", 1, Types.INTEGER)
                        .addValue("salt", salt, Types.CHAR)
                        .addValue("hashed_password", hashed_password, Types.CHAR)
        );


    }

    public void insertRefreshToken(RefreshToken refreshToken){


        LocalDateTime ldt = LocalDateTime.ofInstant(refreshToken.getExpireTime(), ZoneOffset.UTC);
        Timestamp expire = Timestamp.valueOf(ldt);

        LocalDateTime ld = LocalDateTime.ofInstant(refreshToken.getMaxLifeTime(), ZoneOffset.UTC);
        Timestamp max = Timestamp.valueOf(ld);

        int rowsUpdated = this.template.update(

                //This is the SQL query and the : denote varibles being place there
                "INSERT INTO idm.refresh_token (token, user_id, token_status_id, expire_time,max_life_time) " +
                        "VALUES (:token, :user_id, :token_status_id, :expire_time, :max_life_time);",

                new MapSqlParameterSource()
                        .addValue("token", refreshToken.getToken(), Types.CHAR)
                        .addValue("user_id",refreshToken.getUserId() , Types.INTEGER)
                        .addValue("token_status_id", refreshToken.getTokenStatus().id(), Types.INTEGER)
                        .addValue("expire_time", expire, Types.TIMESTAMP)
                        .addValue("max_life_time", max, Types.TIMESTAMP)
        );



    }


    // Checks to see
    public RefreshToken selectRefreshToken(String refreshToken) {

        String sql =
                "SELECT id, token, user_id, token_status_id,expire_time,max_life_time " +
                        "FROM idm.refresh_token " +
                        "WHERE token = :token;"; //notice we mark varaibles with the ':var' format

        MapSqlParameterSource source =
                new MapSqlParameterSource() //For ever ':var' we list a value and `Type` for value
                        .addValue("token", refreshToken, Types.CHAR); // Notice the lack of ':'  in the string here

        try {
            RefreshToken token =
                    this.template.queryForObject(
                            sql,
                            source,
                            (rs, rowNum) ->
                                    new RefreshToken()
                                            .setId(rs.getInt("id"))
                                            .setToken(rs.getString("token"))
                                            .setTokenStatus(getTokenStatus(rs.getInt("token_status_id")))
                                            .setExpireTime(rs.getTimestamp("expire_time").toInstant())
                                            .setMaxLifeTime(rs.getTimestamp("max_life_time").toInstant())
                    );

            return token;
        }catch(EmptyResultDataAccessException e){
            return null;
        }

    }


    public void updateRefreshTokenStatus(RefreshToken token,TokenStatus ts ){

        int rowsUpdated = this.template.update(

                //This is the SQL query and the : denote varibles being place there
                "UPDATE idm.refresh_token "
                        + "SET token_status_id = :token_status_id " +
                        "WHERE token = :token;",
                new MapSqlParameterSource()
                        .addValue("token", token.getToken(), Types.CHAR)
                        .addValue("token_status_id", ts.id(), Types.INTEGER)

        );

    }

    public void updateRefreshTokenExpireTime(RefreshToken token){

        LocalDateTime ld = LocalDateTime.ofInstant(token.getExpireTime(), ZoneOffset.UTC);
        Timestamp time = Timestamp.valueOf(ld);

        int rowsUpdated = this.template.update(

                //This is the SQL query and the : denote varibles being place there
                "UPDATE idm.refresh_token "
                        + "SET expire_time = :expire_time " +
                        "WHERE token = :token;",
                new MapSqlParameterSource()
                        .addValue("token", token.getToken(), Types.CHAR)
                        .addValue("expire_time", time, Types.TIMESTAMP)

        );

        //System.out.println(rowsUpdated);

    }
    public User getUserFromRefreshToken(RefreshToken token){

        // First we have to get the user_id from idm.refresh_token
        String sql =
                "SELECT user_id " +
                        "FROM idm.refresh_token " +
                        "WHERE token = :token;"; //notice we mark varaibles with the ':var' format


        MapSqlParameterSource source =
                new MapSqlParameterSource() //For ever ':var' we list a value and `Type` for value
                        .addValue("token", token.getToken(), Types.CHAR); // Notice the lack of ':'  in the string here

        Integer user_id =
                this.template.queryForObject(
                        sql,
                        source,
                        // For every row this lambda will be called to turn it into a Object (in this case `Student`)
                        (rs, rowNum) ->
                                (rs.getInt("user_id"))
                );



        String sql1 =
                "SELECT id, email, user_status_id, salt, hashed_password " +
                        "FROM idm.user " +
                        "WHERE id = :id;"; //notice we mark varaibles with the ':var' format


        MapSqlParameterSource source1 =
                new MapSqlParameterSource() //For ever ':var' we list a value and `Type` for value
                        .addValue("id", user_id, Types.INTEGER); // Notice the lack of ':'  in the string here

        User user =
                this.template.queryForObject(
                        sql1,
                        source1,
                        // For every row this lambda will be called to turn it into a Object (in this case `Student`)
                        (rs, rowNum) ->
                                new User()
                                        .setId(rs.getInt("id"))
                                        .setEmail(rs.getString("email"))
                                        .setUserStatus(getStatus(rs.getInt("user_status_id")))
                                        .setSalt(rs.getString("salt"))
                                        .setHashedPassword(rs.getString("hashed_password"))
                );


        return user;
    };

}
