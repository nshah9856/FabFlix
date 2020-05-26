package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SingleMovieActivity extends Activity {
  TextView movie_name;
  TextView movie_year;
  TextView movie_director;
  TextView movie_genres;
  TextView movie_stars;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.singlemovie);

    movie_name = findViewById(R.id.movieTitle);
    movie_year = findViewById(R.id.movieYear);
    movie_director = findViewById(R.id.movieDirector);
    movie_genres = findViewById(R.id.movieGenre);
    movie_stars = findViewById(R.id.movieStars);

    Intent intent = this.getIntent();
    String name = intent.getStringExtra("Name");
    String year = intent.getStringExtra("Year");
    String director = intent.getStringExtra("Director");
    String genres = intent.getStringExtra("Genres");
    String stars = intent.getStringExtra("Stars");

    movie_name.setText(name);
    movie_year.setText(year);
    movie_director.setText(director);
    movie_genres.setText(genres.replace(",", ", "));
    movie_stars.setText(stars.replace(",", ", "));

  }
}
