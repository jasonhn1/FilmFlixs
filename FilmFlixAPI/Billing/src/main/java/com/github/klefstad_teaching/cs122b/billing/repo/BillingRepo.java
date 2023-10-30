package com.github.klefstad_teaching.cs122b.billing.repo;

import com.github.klefstad_teaching.cs122b.billing.model.Item;
import com.github.klefstad_teaching.cs122b.billing.response.CartModelResponse;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Component
public class BillingRepo
{
    private NamedParameterJdbcTemplate template;
    @Autowired
    public BillingRepo(NamedParameterJdbcTemplate template)
    {
        this.template = template;
    }

    public Boolean alreadyInCart(Integer userId,Long movieId){

        String sql =
                "SELECT movie_id " +
                        "FROM billing.cart " +
                        "WHERE user_id = :userId;"; //notice we mark varaibles with the ':var' format


        MapSqlParameterSource source =
                new MapSqlParameterSource() //For ever ':var' we list a value and `Type` for value
                        .addValue("userId", userId, Types.VARCHAR); // Notice the lack of ':'  in the string here

        List<Long> movies =
                this.template.query(
                        sql,
                        source,
                        // For every row this lambda will be called to turn it into a Object (in this case `Student`)
                        (rs, rowNum) ->
                                (rs.getLong("movie_id"))
                );

        for(Long id:movies){
            if(id==movieId){
                return true;
            }
        }

        return false;
    }

    public void deleteItem(Integer userId,Long movieId){

//
//        String sql =
//                "SELECT movie_id " +
//                        "FROM billing.cart " +
//                        "WHERE user_id = :userId;"; //notice we mark varaibles with the ':var' format
//
//
//        MapSqlParameterSource source =
//                new MapSqlParameterSource() //For ever ':var' we list a value and `Type` for value
//                        .addValue("userId", userId, Types.VARCHAR); // Notice the lack of ':'  in the string here
//
//        List<Item> items =
//                this.template.query(
//                        sql,
//                        source,
//                        // For every row this lambda will be called to turn it into a Object (in this case `Student`)
//                        (rs, rowNum) ->
//                        new Item()
//                                .setId(rs.getLong("id"))
//                                .setTitle(rs.getString("title"))
//                                .setYear(rs.getInt("year"))
//                                .setDirector(rs.getString("name"))
//                                .setRating(rs.getDouble("rating"))
//                                .setBackdropPath(rs.getString("backdrop_path"))
//                                .setPosterPath(rs.getString("poster_path"))
//                                .setHidden(rs.getBoolean("hidden"))
//                );


    }


    public List<Item> retriveItem(Integer userId,Boolean premium){

        String sql = "";

        if(!premium){

        //language=sql
        sql = "SELECT c.movie_id, s.quantity, mp.unit_price, m.title,m.backdrop_poster,m.poster_path " +
                "FROM billing.cart c "+
                "JOIN billing.sale_item s on c.movie_id = s.movie_id " +
                "JOIN billing.movie_price mp on c.movie_id = mp.movie_id " +
                "JOIN movies.movie m on m.id = c.movie_id " +
                "WHERE user_id = :userId";

        }else{

            //language=sql
            sql = "SELECT c.movie_id, s.quantity, mp.unit_price, mp.premium_discount " +
                    "m.title,m.backdrop_poster,m.poster_path " +
                    "FROM billing.cart c "+
                    "JOIN billing.sale_item s on c.movie_id = s.movie_id " +
                    "JOIN billing.movie_price mp on c.movie_id = mp.movie_id " +
                    "JOIN movies.movie m on m.id = c.movie_id " +
                    "WHERE user_id = :userId";
        }


        MapSqlParameterSource source =
                new MapSqlParameterSource() //For ever ':var' we list a value and `Type` for value
                        .addValue("userId", userId, Types.INTEGER); // Notice the lack of ':'  in the string here




        List<Item> items =
                this.template.query(
                        sql,
                        source,
                        // For every row this lambda will be called to turn it into a Object (in this case `Student`)
                        (rs, rowNum) ->{
                                return new Item()
                                        .setMovieId(rs.getLong("movie_id"))
                                        .setQuantity(rs.getInt("quantity"))
                                        .setUnitPrice(rs.getBigDecimal("unit_price"))
                                        .setMovieTitle(rs.getString("title"))
                                        .setBackdropPath(rs.getString("backdrop_path"))
                                        .setPosterPath(rs.getString("poster_path"));
                        }
                );

        return items;
    }



    public void insertIntoCart(Integer userId,Long movieId,Integer quantity){
        int rowsUpdated = this.template.update(
                "INSERT INTO billing.cart (user_id, movie_id, quantity) " +
                        "VALUES (:userId, :movieId, :quantity);",
                new MapSqlParameterSource()
                        .addValue("userId", userId, Types.INTEGER)
                        .addValue("movieId", movieId, Types.INTEGER)
                        .addValue("quantity", quantity, Types.INTEGER)
        );
    }


    public ResponseEntity updateCart(Integer userId, Long movieId, Integer quantity){
        try {
            int rowsUpdated = this.template.update(
                    "UPDATE billing.cart SET quantity = :quantity " +
                            " WHERE user_id = :userId AND movie_id = :movieId;",
                    new MapSqlParameterSource()
                            .addValue("userId", userId, Types.INTEGER)
                            .addValue("movieId", movieId, Types.INTEGER)
                            .addValue("quantity", quantity, Types.INTEGER)
            );

            if (rowsUpdated == 0) {
                return new CartModelResponse()
                        .setResult(BillingResults.CART_ITEM_DOES_NOT_EXIST)
                        .toResponse();

            }


        }catch (ResultError e ){

            System.out.print(" {?|ERROR---ERROR!|}  No rows were updated  :P");
        }

        return new CartModelResponse()
                .setResult(BillingResults.CART_ITEM_UPDATED)
                .toResponse();
    }



}
