package com.example.copaxon.movies.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class AddFromInternetActivity extends AddActivity {
    private String TAG = AddFromInternetActivity.class.getName();
    protected ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the delivered movie
        movie = (Movie) getIntent().getSerializableExtra("movie");

        //updating the fields that is initialized at the super class
        editTitle.setText(movie.getTitle());
        editBody.setText(movie.getBody());
        editUrl.setText(movie.getUrl());
        ratingBar.setRating(movie.getRating());
        checkIsWatched.setChecked(movie.isWatched());

        //init the imageView
        imageView = (ImageView) findViewById(R.id.imageView);
        try {
            loading.setVisibility(View.VISIBLE);
            // setting the image async
            new EditActivity.DownloadImageTask(imageView)
                    .execute(movie.getUrl());
        }
        catch (Exception e)
        {
            Log.e(TAG, "download image failed", e);
            loading.setText("Failed to load picture");
        }

    }

    //definition of the async task in anonymous class
    protected class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {


        ImageView imageView;
        //constructor that initialize the imageView
        public DownloadImageTask(ImageView bmImage) {
            this.imageView = bmImage;
        }

        //function that gets the url
        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap bitmap = null;
            try {
                // opens the strem to download the image
                InputStream in = new java.net.URL(urlDisplay).openStream();
                // changes the stream to bitmap
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                String TAG = DownloadImageTask.class.getName();
                Log.e(TAG, e.getMessage());
            }
            // returns the bitmap
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            //sets the image to the imageView
            imageView.setImageBitmap(result);
            // hides the loading text
            loading.setVisibility(View.INVISIBLE);
        }
    }
}



