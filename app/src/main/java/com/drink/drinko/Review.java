package com.drink.drinko;

class Review {
    String rating,comment,name;

    public Review() {
    }

    public Review(String rating, String comment, String name) {
        this.rating = rating;
        this.comment = comment;
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
