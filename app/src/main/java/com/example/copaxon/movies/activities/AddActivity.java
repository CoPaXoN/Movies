package com.example.copaxon.movies.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.copaxon.movies.R;
import com.example.copaxon.movies.beans.Movie;
import com.example.copaxon.movies.dao.MoviesDao;

import java.io.InputStream;

public class AddActivity extends AppCompatActivity {

    protected MoviesDao db;
    protected Movie movie;
    protected EditText editTitle;
    protected EditText editBody;
    protected EditText editUrl;
    protected RatingBar ratingBar;
    protected CheckBox checkIsWatched;
    protected Button saveBtn;
    protected Button cancelBtn;
    protected TextView loading;
    private String TAG = AddActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        //init
        db = new MoviesDao(this);
        movie = new Movie();
        editTitle = (EditText) findViewById(R.id.editTitle);
        editBody = (EditText) findViewById(R.id.editBody);
        editUrl = (EditText) findViewById(R.id.editUrl);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        checkIsWatched = (CheckBox) findViewById(R.id.isWatched);

        saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();

            }
        });

        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //loading shows text before picture loaded
        loading = findViewById(R.id.loading);
        loading.setVisibility(View.INVISIBLE);
    }
        //sets new movie with details from the form into the movie object
        protected void setMovieFromForm(){
            String title = editTitle.getText().toString();
            String body = editBody.getText().toString();
            String url = editUrl.getText().toString();
            Float rating = ratingBar.getRating();
            Boolean isWatched = checkIsWatched.isChecked();
            //sets everything but the id to the class variable
           movie.setTitle(title);
           movie.setBody(body);
           movie.setUrl(url);
           movie.setRating(rating);
           movie.setWatched(isWatched);

        }

        // function that called when save button clicked
        protected void save(){
            if(!isFieldsOk()){
                return;
            }

            setMovieFromForm();
            //check if movie exist
            Movie movieToCheckIfMovieExist = null;
            try {
                movieToCheckIfMovieExist = db.getMovie(movie.getTitle(), movie.getBody());
            }
            catch (Exception e){
                Log.e(TAG, "getting movie from db failed", e);
                Toast.makeText(AddActivity.this, "Failed to add movie",
                        Toast.LENGTH_SHORT).show();
            }
            // if movie exist
            if(movieToCheckIfMovieExist != null){
                // shows message that the movie exists
                Toast.makeText(AddActivity.this, "The movie "
                                + movie.getTitle() + " already exist",
                        Toast.LENGTH_SHORT).show();
            }
            // if movie doesn't exist
            else
            {
                try{
                    // adds the movie to the database
                    db.addMovie(movie);
                    // shows message that the movie was added
                    Toast.makeText(AddActivity.this, "The movie "
                            + movie.getTitle() + " was added",
                            Toast.LENGTH_SHORT).show();
                    // returns to the previous activity by using android back function
                    onBackPressed();
                }
                catch (Exception e){
                    Log.e(TAG, "add movie failed", e);
                    Toast.makeText(AddActivity.this, "add movie failed",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        protected Boolean isFieldsOk(){
            if(editTitle.getText().toString().matches("")){
                // shows message that title field is empty
                Toast.makeText(AddActivity.this, "can't save a movie witout a title",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }



