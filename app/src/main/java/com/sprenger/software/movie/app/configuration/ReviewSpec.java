/*
 * Created by Stefan Sprenger
 */

package com.sprenger.software.movie.app.configuration;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.List;

public class ReviewSpec implements ParentObject {

    private List<Object> mChildrenList;

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

    private final String autor;
    private final String review;

    @Override
    public List<Object> getChildObjectList() {
        return mChildrenList;
    }

    @Override
    public void setChildObjectList(List<Object> list) {
        mChildrenList = list;
    }
}
