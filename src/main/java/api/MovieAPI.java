package api;

import models.Movie;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

import java.util.List;

/**
 * Created by Mody on 17-Jan-16.
 */
public interface MovieAPI {

    @GET("/3/search/movie")
    Call<List<Movie>> getMoviesList(@Query("api_key") String apiKey, @Query("query") String query);
}
