package com.github.klefstad_teaching.cs122b.movies.models.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.movies.data.Movie;

import java.util.List;

public class MovieSearchResponse extends ResponseModel<MovieSearchResponse> {

    private List<Movie> movies;

    public List<Movie> getMovies()
    {
        return movies;
    }

    public MovieSearchResponse setMovies(List<Movie> movies)
    {
        this.movies = movies;
        return this;
    }
}
