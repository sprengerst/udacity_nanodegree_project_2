package com.sprenger.software.movie.app;

/**
 * Created by stefa on 24.03.2016.
 */
public class ReviewSpec {


    public ReviewSpec(String autor, String review) {
        this.autor = autor;
        this.review = review;
    }


    public String getAutor() {
        return autor;
    }

    public String getReview() {
        return review;
    }

    private String autor;
    private String review;

}
