package com.github.klefstad_teaching.cs122b.movies.data;

public class Movie {

    private Long    id;
    private String  title;
    private Integer  year;
    private String director;
 //   private String genre;
    private Double  rating;
    private String backdropPath;
    private String posterPath;
    private Boolean hidden;

    public Movie setId(Long id) {
        this.id = id;
        return this;
    }

    public Movie setTitle(String title) {
        this.title = title;
        return this;
    }

    public Movie setYear(Integer year) {
        this.year = year;
        return this;
    }

    public Movie setDirector(String director) {
        this.director = director;
        return this;
    }

//    public Movie setGenre(String genre) {
//        this.genre = genre;
//        return this;
//    }

    public Movie setRating(Double rating) {
        this.rating = rating;
        return this;
    }

    public Movie setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
        return this;
    }

    public Movie setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        return this;
    }

    public Movie setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

//    public String getGenre() {
//        return genre;
//    }

    public Double getRating() {
        return rating;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public Boolean getHidden() {
        return hidden;
    }
}
