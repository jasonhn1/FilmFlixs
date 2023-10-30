package com.github.klefstad_teaching.cs122b.movies.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.klefstad_teaching.cs122b.movies.data.Movie;
import com.github.klefstad_teaching.cs122b.movies.data.MovieOrderBy;
import com.github.klefstad_teaching.cs122b.movies.models.request.MovieSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.List;

@Component
public class MovieRepo
{
    private ObjectMapper objectMapper;
    private NamedParameterJdbcTemplate template;

    @Autowired
    public MovieRepo(ObjectMapper objectMapper, NamedParameterJdbcTemplate template)
    {
        this.objectMapper = objectMapper;
        this.template = template;
    }


    //language=sql
    private final static String MOVIE_NO_GENRE =
            "SELECT DISTINCT m.id,m.title,m.year,p.name,m.rating,m.backdrop_path," +
                    "m.poster_path, m.hidden " +
                    "FROM movies.movie m "+
                    "JOIN movies.person p ON p.id = m.director_id ";


    //language=sql
    private final static String MOVIE_WITH_GENRE =
            "SELECT DISTINCT m.id,m.title,m.year, p.name, m.rating, m.backdrop_path, m.poster_path, m.hidden " +
                    "FROM movies.movie m " +
                    "    JOIN movies.person p ON p.id = m.director_id " +
                    "    JOIN movies.movie_genre mg ON mg.movie_id = m.id " +
                    "    JOIN movies.genre g ON g.id = mg.genre_id";


    public List<Movie> basicMovieSearch(MovieSearchRequest request, Boolean showHidden){


        StringBuilder         sql;
        MapSqlParameterSource source     = new MapSqlParameterSource();
        boolean               whereAdded = false;

/*
genre	String	The movie's genre (Search by substring) WC
title	String	The movie's title (Search by substring) WC
director	String	The movie's director (Search by substring) WC
year	Integer	The movie's release year EXACT
 */


        if (request.getGenre() != null) {
            sql = new StringBuilder(MOVIE_WITH_GENRE);
            sql.append(" WHERE g.name LIKE :genreName ");

            // This allows for WILDCARD Search
            String wildcardSearch = '%' + request.getGenre() + '%';

            source.addValue("genreName", wildcardSearch, Types.VARCHAR);
            whereAdded = true;
        } else {
            sql = new StringBuilder(MOVIE_NO_GENRE);
        }


        if (request.getTitle() != null) {
            if (whereAdded) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                whereAdded = true;
            }

            String wildcardSearch = '%' + request.getTitle() + '%';
            sql.append(" m.title LIKE :title ");
            source.addValue("title", wildcardSearch, Types.VARCHAR);
        }



        if (request.getDirector() != null) {
            if (whereAdded) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                whereAdded = true;
            }


            // %anthony russo%
            String wildcardSearch = '%' + request.getDirector() + '%';
            sql.append(" p.name LIKE :directorName ");

            source.addValue("directorName", wildcardSearch, Types.VARCHAR);

        }


        if (request.getYear() != null) {
            if (whereAdded) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                whereAdded = true;
            }

            sql.append(" m.year = :year ");
            source.addValue("year", request.getYear(), Types.INTEGER);
        }

    /*
    limit	Integer	Number of movies to list at one time: 10 (default), 25, 50, or 100
    page	Integer	The page for pagination: 1 (default) or any positive number over 0
    orderBy	String	Sorting parameter: title (default) or rating or year
    direction	String	Sorting direction: asc (default) or desc
    * */



        //ORDER BY m.title, m.id
        //String orderBy = " ORDER BY m."+ request.getOrderBy()+" "+ request.getDirection()+", m.id "+ request.getDirection() ;

        MovieOrderBy orderBy = MovieOrderBy.fromString(request.getOrderBy(), request.getDirection());

        sql.append(orderBy.toSql());


        //((page - 1) * limit)
        Integer offset = ((request.getPage() - 1) * request.getLimit());

        // LIMIT 5 OFFSET
        String limit = " LIMIT "+ request.getLimit()+" OFFSET "+ offset;
        sql.append(limit);

        // we have to do asc and desc
        if(showHidden){
            List<Movie> movies = this.template.query(
                    sql.toString(),
                    source,
                    (rs, rowNum) ->
                            new Movie()
                                    .setId(rs.getLong("id"))
                                    .setTitle(rs.getString("title"))
                                    .setYear(rs.getInt("year"))
                                    .setDirector(rs.getString("name"))
                          //          .setGenre(rs.getString("genre"))
                                    .setRating(rs.getDouble("rating"))
                                    .setBackdropPath(rs.getString("backdrop_path"))
                                    .setPosterPath(rs.getString("poster_path"))
                                    .setHidden(rs.getBoolean("hidden"))
            );

            return movies;
        }else {

            List<Movie> movies = this.template.query(
                    sql.toString(),
                    source,
                    (rs, rowNum) ->
                            new Movie()
                                    .setId(rs.getLong("id"))
                                    .setTitle(rs.getString("title"))
                                    .setYear(rs.getInt("year"))
                                    .setDirector(rs.getString("name"))
                           //         .setGenre(rs.getString("genre"))
                                    .setRating(rs.getDouble("rating"))
                                    .setBackdropPath(rs.getString("backdrop_path"))
                                    .setPosterPath(rs.getString("poster_path"))
                                   // .setHidden(rs.getBoolean("hidden"))

            );

            return movies;

        }


    }

}
