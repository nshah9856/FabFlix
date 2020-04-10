import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//Declare WebServlet
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/movie")
public class SingleMovieServlet extends HttpServlet {
  private static final long serialVersionUID = 2L;

  // Create a dataSource which registered in web.xml
  @Resource(name = "jdbc/moviedb")
  private DataSource dataSource;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setContentType("application/json"); // Response mime type

    // Retrieve parameter id from url request.
    String id = request.getParameter("id");

    // Output stream to STDOUT
    PrintWriter out = response.getWriter();

    try {
      // Get a connection from dataSource
      Connection connection = dataSource.getConnection();

      // Construct a query with parameter represented by "?"
      String query = "SELECT m.*, s.name as star,s.id as starId, g.name as genre, r.rating FROM movies m " +
              "INNER JOIN stars_in_movies sm ON sm.movieId = m.id " +
              "INNER JOIN genres_in_movies gm ON gm.movieId = m.id " +
              "INNER JOIN stars s ON s.id = sm.starId " +
              "INNER JOIN genres g ON  g.id = gm.genreId " +
              "INNER JOIN ratings r ON r.movieId = m.id "+
              "WHERE m.id = ?";

      // Declare our statement
      PreparedStatement statement = connection.prepareStatement(query);

      // Set the parameter represented by "?" in the query to the movie id we get from url,
      // num 1 indicates the first "?" in the query
      statement.setString(1, id);

      ResultSet resultSet = statement.executeQuery();

      JsonArray jsonArray = new JsonArray();

      // Iterate through each row of resultSet
      while (resultSet.next()) {
        String movie_title = resultSet.getString("title");
        String movie_year = resultSet.getString("year");
        String movie_director = resultSet.getString("director");
        String movie_genres = resultSet.getString("genre");
        String movie_stars = resultSet.getString("star");
        String movie_star_id = resultSet.getString("starId");
        String movie_ratings = resultSet.getString("rating");

        // Create a JsonObject based on the data we retrieve from resultSet
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("movie_title", movie_title);
        jsonObject.addProperty("movie_year", movie_year);
        jsonObject.addProperty("movie_director", movie_director);
        jsonObject.addProperty("movie_genres", movie_genres);
        jsonObject.addProperty("movie_stars", movie_stars);
        jsonObject.addProperty("movie_star_id", movie_star_id);
        jsonObject.addProperty("movie_ratings", movie_ratings);
        jsonArray.add(jsonObject);
      }

      // write JSON string to output
      out.write(jsonArray.toString());
      // set response status to 200 (OK)
      response.setStatus(200);

      resultSet.close();
      statement.close();
      connection.close();
    } catch (Exception e) {

      // write error message JSON object to output
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("errorMessage", e.getMessage());
      out.write(jsonObject.toString());

      // set response status to 500 (Internal Server Error)
      response.setStatus(500);

    }
    out.close();

  }
}