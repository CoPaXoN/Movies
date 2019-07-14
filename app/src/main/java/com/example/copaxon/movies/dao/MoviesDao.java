package com.example.copaxon.movies.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.copaxon.movies.Utils.MovieDbConstants;
import com.example.copaxon.movies.beans.Movie;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by CoPaXoN on 29/07/2018.
 */

public class MoviesDao {
    private DbHelper dbhelper;
    private final static String LOG_TAG = MoviesDao.class.getName();
    private SQLiteDatabase db;
    public MoviesDao(Context context) {
        dbhelper = new DbHelper(context, MovieDbConstants.DATABASE_NAME, null,
                MovieDbConstants.DATABASE_VERSION);
    }

    public ArrayList<Movie> getAllMovies() {
        db = dbhelper.getReadableDatabase();

        try {
            Cursor cursor = db.query(MovieDbConstants.TABLE_NAME, null, null,
                    null, null, null, null);
            ArrayList<Movie> moviesArrayList = new ArrayList<Movie>();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(MovieDbConstants.COL_ID));
                String title = cursor.getString(cursor.getColumnIndex(MovieDbConstants.COL_TITLE));
                String body = cursor.getString(cursor.getColumnIndex(MovieDbConstants.COL_BODY));
                String url = cursor.getString(cursor.getColumnIndex(MovieDbConstants.COL_URL));
                float rating = cursor.getFloat(cursor.getColumnIndex(MovieDbConstants.COL_RATING));
                int isWatchedInt = cursor.getInt(cursor.getColumnIndex(MovieDbConstants.COL_ISWATCHED));
                boolean isWatched = (isWatchedInt == 1) ? true : false;
                moviesArrayList.add(new Movie(id, title, body, url, rating, isWatched));
            }
            return moviesArrayList;
        }
        catch (Exception e){
            Log.e(LOG_TAG, "Failed to get all movies",e);
            throw e;
        }
    }

    public Movie getMovie(String title, String body){
        db = dbhelper.getReadableDatabase();
        Movie movie = null;

        try {
            Cursor cursor = db.query(MovieDbConstants.TABLE_NAME, null, MovieDbConstants.COL_TITLE + "=? and "
                            + MovieDbConstants.COL_BODY + "=?",
                    new String[]{String.valueOf(title), String.valueOf(body)}, null, null, null, null);
            // Check if the movie was found
            if (cursor.moveToFirst()) {

                int movieId = cursor.getInt(cursor.getColumnIndex(MovieDbConstants.COL_ID));
                //the movie title and body is given so there is not need to get them
                String url = cursor.getString(cursor.getColumnIndex(MovieDbConstants.COL_URL));
                float rating = cursor.getFloat(cursor.getColumnIndex(MovieDbConstants.COL_RATING));
                int isWatched = cursor.getInt(cursor.getColumnIndex(MovieDbConstants.COL_ISWATCHED));
                // short if (skill! bitch)
                boolean isWatchedBool = (isWatched == 1)?true:false;

                movie = new Movie(movieId, title,body,url,rating,isWatchedBool);
            }
            return movie;
        }
        catch (Exception e){
            Log.e(LOG_TAG, "Failed to get movie",e);
            throw e;
        }

    }

    // Adding a new movie
    public void addMovie(Movie movie) {
        db = dbhelper.getWritableDatabase();
        ContentValues newMovieValues = new ContentValues();

        newMovieValues.put(MovieDbConstants.COL_TITLE, movie.getTitle());
        newMovieValues.put(MovieDbConstants.COL_BODY, movie.getBody());
        newMovieValues.put(MovieDbConstants.COL_URL, movie.getUrl());
        newMovieValues.put(MovieDbConstants.COL_RATING, movie.getRating());
        newMovieValues.put(MovieDbConstants.COL_ISWATCHED, movie.isWatched());

        try {
            db.insertOrThrow(MovieDbConstants.TABLE_NAME, null, newMovieValues);
        }
        catch (Exception e){
            Log.e(LOG_TAG, "Failed to add movie", e);
            throw  e;
        }
        finally {
            db.close();
        }
    }

    // Update a movie
    public void updateMovie(Movie movie) {
        db = dbhelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(MovieDbConstants.COL_TITLE, movie.getTitle());
            values.put(MovieDbConstants.COL_BODY, movie.getBody());
            values.put(MovieDbConstants.COL_URL, movie.getUrl());
            values.put(MovieDbConstants.COL_RATING, movie.getRating());
            values.put(MovieDbConstants.COL_ISWATCHED, movie.isWatched());
            db.update(MovieDbConstants.TABLE_NAME, values, MovieDbConstants.COL_ID + "=?",
                    new String[]{String.valueOf(movie.getId())});
        }
        catch (Exception e){
            Log.e(LOG_TAG, "Failed to update movie", e);
            throw  e;
        }
        finally {
            db.close();
        }
    }

    // Delete a movie
    public void deleteMovie(int id) {

        db = dbhelper.getWritableDatabase();
        try {
            db.delete(MovieDbConstants.TABLE_NAME, MovieDbConstants.COL_ID + "=?",
                    new String[]{String.valueOf(id)});
        }
        catch (Exception e){
            Log.e(LOG_TAG, "Failed to delete movie", e);
            throw  e;
        }
        finally {
            db.close();
        }
    }


    // Delete all Movies
    public void deleteAllMovies(){
        db = dbhelper.getWritableDatabase();
        try {
            db.delete(MovieDbConstants.TABLE_NAME, null, null);
        }
        catch (Exception e){
            Log.e(LOG_TAG, "Failed to delete all movies", e);
            throw  e;
        }
        finally {
            db.close();
        }

    }
    // checks how much instances of the same movie is there,
    // used in edit form if there is 2 movies with the same title and body the movie won't be saved
    public int getMoviesCount(String title, String body) {
        db = dbhelper.getReadableDatabase();
        try {
            Cursor cursor = db.query(MovieDbConstants.TABLE_NAME, null, MovieDbConstants.COL_TITLE + "=? and "
                            + MovieDbConstants.COL_BODY + "=?",
                    new String[]{String.valueOf(title), String.valueOf(body)}, null, null, null, null);
            return cursor.getCount();
        }
        catch (Exception e){
            Log.e(LOG_TAG, "Failed to get movies count", e);
            throw  e;
        }
        finally {
            db.close();
        }

    }
}
