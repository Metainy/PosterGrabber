package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mody on 17-Jan-16.
 */
public class Movie {

    @SerializedName("id")
    private int movieID;
    @SerializedName("original_title")
    private String movieTitle;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("overview")
    private String movieOverview;
    @SerializedName("poster_path")
    private String posterURL;
    @SerializedName("backdrop_path")
    private String backdropURL;
    @SerializedName("vote_average")
    private double movieRate;

    public int getMovieID() {
        return movieID;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getReleaseDate() {
        return releaseDate.split("-")[0];
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getMovieOverview() {
        return movieOverview;
    }

    public void setMovieOverview(String movieOverview) {
        this.movieOverview = movieOverview;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public String getBackdropURL() {
        return backdropURL;
    }

    public void setBackdropURL(String backdropURL) {
        this.backdropURL = backdropURL;
    }

    public double getMovieRate() {
        return movieRate;
    }

    public void setMovieRate(double movieRate) {
        this.movieRate = movieRate;
    }
}
