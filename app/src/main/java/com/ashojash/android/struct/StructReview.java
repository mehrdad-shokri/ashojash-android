package com.ashojash.android.struct;
@Deprecated
public class StructReview {
    private String comment;
    private int cost;
    private int score;
    private int decor;
    private String userImageUrl;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getScore() {
        return score;
    }

    public void setQuality(int score) {
        this.score = score;
    }

    public int getDecor() {
        return decor;
    }

    public void setDecor(int decor) {
        this.decor = decor;
    }


    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }
}
