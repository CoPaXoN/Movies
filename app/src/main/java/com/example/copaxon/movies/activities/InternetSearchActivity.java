package com.example.copaxon.movies.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.copaxon.movies.R;
import com.example.copaxon.movies.beans.Movie;
import com.example.copaxon.movies.dao.MoviesDao;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class InternetSearchActivity extends AppCompatActivity {

    private EditText editSearch;
    private Movie movie;
    private ArrayList<Movie> moviesArrayList;
    private ArrayAdapter<Movie> adapter;
    private ListView listView;
    private MoviesDao db;
    private final String TAG = InternetSearchActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        moviesArrayList = new ArrayList<>();
        adapter = new ArrayAdapter<Movie>(getApplicationContext(), android.R.layout.simple_list_item_1,moviesArrayList);
        listView = (ListView) findViewById(R.id.viewListMovies);
        listView.setAdapter(adapter);

        Button goBtn = (Button)findViewById(R.id.goBtn);

        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetMovie().execute();
            }
        });
        try {
            db = new MoviesDao(this);
        }
        catch (Exception e){
            Log.e(TAG, "couldn't connect to database", e);
            Toast.makeText(getApplicationContext(), "Failed to connect to database", Toast.LENGTH_SHORT);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // put the chosen movie in the class variable
                movie = (Movie) adapterView.getItemAtPosition(i);
                // trying to find the same movie
                Movie movieToCheckIfMovieExist = null;
                try {
                    movieToCheckIfMovieExist = db.getMovie(movie.getTitle(), movie.getBody());
                }
                catch (Exception e){
                    Log.e(TAG, "couldn't connect to database", e);
                    Toast.makeText(InternetSearchActivity.this, "Couldn't get movie", Toast.LENGTH_SHORT);
                }
                if(movieToCheckIfMovieExist != null) {
                    Toast.makeText(InternetSearchActivity.this,
                            "The movie " + movie.getTitle() + " already exist",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // go to add from internt activity with the movie if there is no such movie
                Intent intent = new Intent(InternetSearchActivity.this, AddFromInternetActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        });

    }

    public class GetMovie extends AsyncTask<String, Void, ArrayList<Movie>> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            // shows progress dialog
            progressDialog = new ProgressDialog(InternetSearchActivity.this);
            progressDialog.setMessage("Loading Your data,please wait.");
            progressDialog.show();
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            StringBuilder builder = new StringBuilder();

            try {

                editSearch = findViewById(R.id.editSearch);
                String search = editSearch.getText().toString();
                //its not bug, its feature
                if(search.matches("")){
                    search = "nothing";
                }
                //changes the search in case there is spaces
                search = search.replace(" ","%20");
                // clears before get the new results
                moviesArrayList.clear();

                // Sending a GET request
                URL url = new URL("https://api.themoviedb.org/3/search/movie?api_key=af7e18658290204b3dfef2699b639b65&query=" + search);

                // Performing the GET call to the server
                connection = (HttpURLConnection) url.openConnection();

                // If the response from the server is not OK (the request failed)
                // Then we return an error message and stop here.
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Failed to get movie", new Exception());
                }

                // We read the response into a StringBuilder object, to which we insert the JSON
                // object, which returned from the server
                // Remember - a json object can be an array by itself, an array of JSON objects
                //----------------------------------------------------------------------------------
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                //----------------------------------------------------------------------------------


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "search failed", e);
                    }
                }
            }
            String responseFromServer = builder.toString();

            if (!responseFromServer.startsWith("Error")) {

                // Close the graphic view which shows an indication of a long wait for the user
                progressDialog.dismiss();

                JsonParser jp=new JsonParser();
                JsonElement element = jp.parse(responseFromServer);
                JsonObject jsonObject = element.getAsJsonObject();
                JsonArray results = jsonObject.getAsJsonArray("results");


                Iterator iterator = results.iterator();

                // adds the objects to the array
                while (iterator.hasNext()) {
                    JsonElement delement = (JsonElement) iterator.next();

                    String title = String.valueOf((JsonElement) delement.getAsJsonObject().get("title"));
                    String overview = String.valueOf((JsonElement) delement.getAsJsonObject().get("overview"));
                    String urlImage = String.valueOf((JsonElement) delement.getAsJsonObject().get("poster_path"));
                    urlImage = "https://image.tmdb.org/t/p/w500"+ urlImage;
                    String averageVote = String.valueOf((JsonElement) delement.getAsJsonObject().get("vote_average"));
                    float voteAverage = Float.parseFloat(averageVote);

                    movie = new Movie(title, overview, urlImage, voteAverage, false);
                    moviesArrayList.add(movie);
                }

                if (responseFromServer == null || responseFromServer.matches("")) {
                    Toast.makeText(getApplicationContext(), "Coudn't find anything", Toast.LENGTH_SHORT).show();
                }
            }
            return moviesArrayList;
        }

        protected void onPostExecute(ArrayList<Movie> moviesArrayList) {
            // We validate that the response does not start with the word Error ("Error From Server...")
            // That way we know it is safe to parse and display the data.
            adapter.notifyDataSetChanged();
        }
    }

    // saves instant state when changing device orientation
    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("movies",moviesArrayList);
    }
    // loads saves instant state when changing device orientation
    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        this.moviesArrayList.clear();
        this.moviesArrayList.addAll((ArrayList<Movie>) state.getSerializable("movies"));
        this.adapter.notifyDataSetChanged();
    }
}