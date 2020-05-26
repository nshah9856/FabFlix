package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListViewActivity extends Activity {
    private int page, totalRows;
    private String search, url;
    private ArrayList<Movie> movies;
    Button prev, next;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        Intent intent = this.getIntent();
        page = Integer.parseInt(intent.getStringExtra("page"));
        search = intent.getStringExtra("search");

        prev = findViewById(R.id.prevButton);
        next = findViewById(R.id.nextButton);

        //this should be retrieved from the database and the backend server
        movies = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(intent.getStringExtra("movies"));
            for(int i = 0; i < arr.length(); ++i){
                JSONObject j = arr.getJSONObject(i);
                System.out.println(j.getString("movie_title"));
                System.out.println(j.getString("movie_year"));
                System.out.println(j.getString("movie_director"));
                System.out.println("genres: " + j.getString("movie_genres"));
                System.out.println("stars: " + j.getString("movie_stars_name"));
                movies.add(new Movie(j.getString("movie_title"),
                    (short) Integer.parseInt(j.getString("movie_year")),
                    j.getString("movie_director"),
                    j.getString("movie_genres"),
                    j.getString("movie_stars_name")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);

        listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
                //TODO: link to a new page here
                viewPage(movie);

//                String message = String.format("Clicked on position: %d, name: %s, %d, %s, %s, %s",
//                    position
//                    , movie.getName(), movie.getYear(), movie.getDirector(),
//                    movie.getGenres(), movie.getStars());
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
         url = "https://54.149.230.122:8443/FabFlix/api/";

         getTotalPages();

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevPage();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextPage();
            }
        });
    }

    public void viewPage(Movie movie){
//        System.out.println("VIEW PAGE CALLED WITH THIS MOVIE: " + movie.getName()); //good
        Intent singleMovie = new Intent(ListViewActivity.this, SingleMovieActivity.class);
        singleMovie.putExtra("Name", movie.getName());
        singleMovie.putExtra("Year", movie.getYear() + "");
        singleMovie.putExtra("Director", movie.getDirector());
        singleMovie.putExtra("Genres", movie.getGenresAll());
        singleMovie.putExtra("Stars", movie.getStarsAll());
        startActivity(singleMovie);
    }

    private void prevPage() {
        if(page - 1 < 0){
            Toast.makeText(getApplicationContext(), "First Page. No prev page available.",
                Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            --page;
            //do all this iin the bottom...
        }
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        //request type is POST
        final StringRequest searchRequest = new StringRequest(Request.Method.POST, url + "search", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO should parse the json response to redirect to appropriate functions.
                Log.d("login.success", response);
                try {
                    JSONArray res = new JSONArray(response);
                    movies.clear();
                    for(int i = 0; i< res.length(); ++i) {
                        JSONObject j = res.getJSONObject(i);
                        movies.add(new Movie(j.getString("movie_title"),
                            (short) Integer.parseInt(j.getString("movie_year")),
                            j.getString("movie_director"),
                            j.getString("movie_genres"),
                            j.getString("movie_stars_name")
                        ));
                    }

                    MovieListViewAdapter adapter = new MovieListViewAdapter(movies,
                        ListViewActivity.this);
                    listView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error
                    Log.d("login.error", error.toString());
                }
            }) {
            @Override
            protected Map<String, String> getParams() {
                // Post request form data
                final Map<String, String> params = new HashMap<>();
                params.put("title", search);
                params.put("filter_search", "true");
                params.put("limit", "20");
                params.put("offset", (page * 20) + "");
                return params;
            }
        };

        // !important: queue.add is where the login request is actually sent
        queue.add(searchRequest);
    }

    private void nextPage() {
        if(page + 1 >= totalRows){
           Toast.makeText(getApplicationContext(), "Last Page. No next page available.",
                Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            ++page;
            //do all this iin the bottom...
        }
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        //request type is POST
        final StringRequest searchRequest = new StringRequest(Request.Method.POST, url + "search", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO should parse the json response to redirect to appropriate functions.
                Log.d("login.success", response);
                try {
                    JSONArray res = new JSONArray(response);
                    movies.clear();
                    for(int i = 0; i< res.length(); ++i) {
                        JSONObject j = res.getJSONObject(i);
                        movies.add(new Movie(j.getString("movie_title"),
                            (short) Integer.parseInt(j.getString("movie_year")),
                            j.getString("movie_director"),
                            j.getString("movie_genres"),
                            j.getString("movie_stars_name")
                        ));
                    }

                    MovieListViewAdapter adapter = new MovieListViewAdapter(movies,
                        ListViewActivity.this);
                    listView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error
                    Log.d("login.error", error.toString());
                }
            }) {
            @Override
            protected Map<String, String> getParams() {
                // Post request form data
                final Map<String, String> params = new HashMap<>();
                params.put("title", search);
                params.put("filter_search", "true");
                params.put("limit", "20");
                params.put("offset", (page * 20) + "");
                return params;
            }
        };

        // !important: queue.add is where the login request is actually sent
        queue.add(searchRequest);
    }

    private void getTotalPages() {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        //request type is POST
        final StringRequest searchRequest = new StringRequest(Request.Method.POST, url +
            "pagecount",
            new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO should parse the json response to redirect to appropriate functions.
                Log.d("login.success", response);
                try {
                    JSONObject res = new JSONObject(response);
//                    System.out.println("HERE, RESPONSE:" + response);
//                    Toast.makeText(getApplicationContext(), response,
//                        Toast.LENGTH_SHORT).show();
                    totalRows = (int) Math.round(Math.ceil(Integer.parseInt(res.getString("title_count")) / 20.0));
//                    System.out.println("TOTAL ROWS.. " + Integer.parseInt(res.getString("title_count")) / 20.0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error
                    Log.d("login.error", error.toString());
                }
            }) {
            @Override
            protected Map<String, String> getParams() {
                // Post request form data
                final Map<String, String> params = new HashMap<>();
                params.put("title", search);
                params.put("filter_search", "true");
                return params;
            }
        };
        queue.add(searchRequest);
    }
}