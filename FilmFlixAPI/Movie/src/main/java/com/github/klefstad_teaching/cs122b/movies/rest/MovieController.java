package com.github.klefstad_teaching.cs122b.movies.rest;

import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.movies.data.Movie;
import com.github.klefstad_teaching.cs122b.movies.models.request.MovieSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.models.response.MovieSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.repo.MovieRepo;
import com.github.klefstad_teaching.cs122b.movies.util.Validate;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;

import java.text.ParseException;
import java.util.List;

@RestController
public class MovieController
{
    private final MovieRepo repo;
    private final Validate validate;

    @Autowired
    public MovieController(MovieRepo repo, Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }
/*
title	String	The movie's title (Search by substring) WC
year	Integer	The movie's release year EXACT
director	String	The movie's director (Search by substring) WC
genre	String	The movie's genre (Search by substring) WC


limit	Integer	Number of movies to list at one time: 10 (default), 25, 50, or 100
page	Integer	The page for pagination: 1 (default) or any positive number over 0
orderBy	String	Sorting parameter: title (default) or rating or year
direction	String	Sorting direction: asc (default) or desc
 */



    @GetMapping("/movie/search")
    public ResponseEntity<MovieSearchResponse> helloFullName(
            @AuthenticationPrincipal SignedJWT user,
            MovieSearchRequest request
            ){


        // First, lets filter through the bad user input and
        // Throw the corresponding Errors
        switch (request.getDirection()) {
            case "asc": case "desc":
                break;
            default:
                return new MovieSearchResponse()
                        .setResult(MoviesResults.INVALID_DIRECTION)
                        .toResponse();
        }

        switch (request.getOrderBy()) {
            case "title": case "rating": case "year":
                break;
            default:
                return new MovieSearchResponse()
                        .setResult(MoviesResults.INVALID_ORDER_BY)
                        .toResponse();
        }

        switch (request.getLimit()) {
            case 10: case 25: case 50: case 100:
                break;
            default:
                return new MovieSearchResponse()
                        .setResult(MoviesResults.INVALID_LIMIT)
                        .toResponse();
        }

        if(request.getPage() <= 0){
            return new MovieSearchResponse()
                    .setResult(MoviesResults.INVALID_PAGE)
                    .toResponse();
        }

        // Second lets check to see the user's roles
        Boolean showHidden = false;
        try{
            List<String> roles = user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);
            //Admin or Employee
            for(String role:roles){
                if(role.equals("ADMIN") || role.equals("EMPLOYEE")){
                    showHidden = true;
                }
            }
        }catch (ParseException e){

        }


        // Finally we use repo to search for movies
        List<Movie> searchedMovies = repo.basicMovieSearch(request,showHidden);


        //If the search comes back with nothing
        if(searchedMovies.isEmpty()){
            return new MovieSearchResponse()
                    .setResult(MoviesResults.NO_MOVIES_FOUND_WITHIN_SEARCH)
                    .toResponse();
        }else{
            return new MovieSearchResponse()
                    .setResult(MoviesResults.MOVIES_FOUND_WITHIN_SEARCH)
                    .setMovies(searchedMovies)
                    .toResponse();
        }

    }





}
