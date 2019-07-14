package com.example.copaxon.movies.activities;

import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

public class EditActivity extends AddFromInternetActivity {

    private String TAG = EditActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Override the saveBtnClick
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // checks if the fields are legal with super class function
            if(!isFieldsOk()){
                return;
            }
            //sets movie fields into the class variable movie
            setMovieFromForm();
            //gets how much same movies like the one from the form are in the database\
            try {
                int moviesCount = db.getMoviesCount(movie.getTitle(), movie.getBody());
                // if movie exist more then once
                // means the movie you trying to edit
                // is already exist as other movie
                if(moviesCount > 1){
                    // shows message that the movie exists
                    Toast.makeText(EditActivity.this,
                            "The movie with the same title and body already exist",
                            Toast.LENGTH_SHORT).show();
                }
                // if it's not exist
                else {
                    try {
                        // edit the movie details
                        db.updateMovie(movie);
                        // shows message that the movie was edited
                        Toast.makeText(EditActivity.this, "The movie "
                                + movie.getTitle() + " was edited", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to edit movie", e);
                        Toast.makeText(EditActivity.this,
                                "Failed to edit movie",
                                Toast.LENGTH_SHORT).show();

                    }
                    //returning to the previous activity by using android back function
                    onBackPressed();
                }
            }
            catch (Exception e) {
                Log.e(TAG, "couldn't get movies count from database", e);
                Toast.makeText(EditActivity.this,
                        "Failed  to edit movie",
                        Toast.LENGTH_SHORT).show();

            }
            }
        });
    }
}

