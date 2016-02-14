package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Movie;
import retrofit.*;
import ui.Controller;
import util.Constants;
import util.CustomDeserializer;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Mody on 17-Jan-16.
 */
public class MovieAPIImpl {

    private MovieAPI movieAPI;

    public MovieAPIImpl() {

        // Creating custom Gson Object
        Gson gson = new GsonBuilder().registerTypeAdapter(List.class, new CustomDeserializer<List<Movie>>()).create();

        // Creating the Retrofit instance, passing the base URL, and custom Gson object
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.SERVICE_BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
        movieAPI = retrofit.create(MovieAPI.class);
    }

    public void getMovieList(String apiKey, String query, Controller controller) {

        Call<List<Movie>> call = movieAPI.getMoviesList(apiKey, query);

        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Response<List<Movie>> response, Retrofit retrofit) {

                // Check if there is a poster associated with the movie, if it's not it gets removed from the list
                Iterator<Movie> movieIterator = response.body().iterator();

                while (movieIterator.hasNext()) {

                    if (movieIterator.next().getPosterURL() == null) {

                        movieIterator.remove();
                    }
                }

                // Converting the movie response list
                ObservableList<Movie> movieList = FXCollections.observableArrayList(response.body());

                // Callback to the Controller
                ((Controller) controller).onMoviesReceive(movieList);
            }

            @Override
            public void onFailure(Throwable throwable) {

                // Callback to the Controller
                ((Controller) controller).onMoviesError();
            }
        });
    }
}
