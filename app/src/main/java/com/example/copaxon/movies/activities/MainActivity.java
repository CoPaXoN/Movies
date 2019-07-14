package com.example.copaxon.movies.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.copaxon.movies.R;
import com.example.copaxon.movies.Utils.AddType;
import com.example.copaxon.movies.beans.Movie;
import com.example.copaxon.movies.dao.MoviesDao;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button addBtn;
    private ArrayAdapter<Movie> adapter;
    private ListView listView;
    private ArrayList<Movie> moviesArrayList;
    private MoviesDao db;
    private final String TAG = InternetSearchActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init
        addBtn = (Button) findViewById(R.id.addBtn);
        // on add button click
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create menu internet, manual
                PopupMenu addOptions = new PopupMenu(MainActivity.this, addBtn);
                addOptions.getMenuInflater().inflate(R.menu.add_options, addOptions.getMenu());
                // on item menu clicked
                addOptions.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.internet:
                                gotoAddActivity(AddType.INTERNET);
                                return true;

                            case R.id.manual:
                                gotoAddActivity(AddType.MANUAL);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //shows the menu
                addOptions.show();
            }
        });
        listView = (ListView) findViewById(R.id.viewListMovies);
        db = new MoviesDao(MainActivity.this);

        moviesArrayList = new ArrayList();
        addAllMovies();
        adapter = new ArrayAdapter<Movie>(this, android.R.layout.simple_list_item_1, moviesArrayList);

        // Connecting the array adapter to the listview
        listView.setAdapter(adapter);

        //this.editText = (EditText) findViewById(R.id.editText1);
        registerForContextMenu(listView);

        // Registering the list view to a single short click event (callback)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) parent.getItemAtPosition(position);
                gotoEditActivity(movie);
            }
        });
    }

    // function that goes to the add activity by type(enum) delivered
    public void gotoAddActivity(AddType addType) {
        if (addType == AddType.INTERNET) {
            Intent intent = new Intent(this, InternetSearchActivity.class);
            startActivity(intent);
        }
        if (addType == AddType.MANUAL) {
            Intent intent = new Intent(this, AddActivity.class);
            startActivity(intent);
        }
    }

    //function that goes to edit activity with movie
    public void gotoEditActivity(Movie movie) {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    // android function that takes action when long touching the line in the listView
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        //shows edit or delete options
        inflater.inflate(R.menu.movie_options, menu);
    }

    // android fucntion that called when Item selected on actionBar menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // gets the info of the item in the list with adapter
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        // gets the position from the info of the item and getting the position in the list
        final Movie movie = ((Movie) adapter.getItem(menuInfo.position));

        //checks the its id that was clicked
        switch (item.getItemId()) {
            case R.id.edit:
                gotoEditActivity(movie);
                return true;
            case R.id.delete:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MainActivity.this);

                // set dialog message
                alertDialogBuilder
                        .setTitle("Are you sure you want delete " + movie.getTitle() + "?")
                        //.setMessage("Click yes delete")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    db.deleteMovie(movie.getId());
                                }
                                catch (Exception e){
                                    Log.e(TAG, "delete movie failed", e);
                                    Toast.makeText(MainActivity.this, "Failed to delete movie", Toast.LENGTH_SHORT);
                                }
                                updateList();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();



                return true;
        }
        return super.onContextItemSelected(item);
    }
    //when you click on the action bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        // shows menu delete all and exit
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //when something is selected from the actions bar menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.deleteAll:
                deleteAll();
                break;
            case R.id.quit:
                System.exit(0);
        }
        return super.onOptionsItemSelected(item);
    }

    //function that delete all the movies
    public void deleteAll(){
        try {
            db.deleteAllMovies();
        }
        catch (Exception e){
            Log.e(TAG, "Failed to delete all movies", e);
            Toast.makeText(MainActivity.this, "Failed to delete all movies", Toast.LENGTH_SHORT);
        }
        moviesArrayList.clear();
        adapter.notifyDataSetChanged();
    }

//

    //when resuming from other activity
    @Override
    protected void onResume() {
        super.onResume();
        // updates the list of movies
        updateList();

    }
    // function that updates the movies list
    public void updateList(){
        this.moviesArrayList.clear();
        addAllMovies();
        this.adapter.notifyDataSetChanged();
    }

    // function that sets all movies into the class array list
    public void addAllMovies()
    {
        try {
            this.moviesArrayList.addAll(db.getAllMovies());
        }
        catch (Exception e){
            Log.e(TAG, "Failed to get all movies", e);
            Toast.makeText(MainActivity.this, "Failed to get all movies", Toast.LENGTH_SHORT);
        }
    }
}
