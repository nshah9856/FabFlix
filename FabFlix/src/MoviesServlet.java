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
import java.sql.ResultSet;
import java.sql.Statement;

//Declare WebServlet
@WebServlet(name = "MoviesServlet", urlPatterns = "/movies")
public class MoviesServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  // Create a dataSource which registered in web.xml
  @Resource(name = "jdbc/moviedb")
  private DataSource dataSource;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("application/json"); // Response mime type
    response.setCharacterEncoding("UTF-8");

    // Output stream to STDOUT
    PrintWriter out = response.getWriter();

    try {
      // Get a connection from dataSource
      Connection connection = dataSource.getConnection();

      // Declare our statement
      Statement statement = connection.createStatement();

      String query = "SELECT m.*, s.name as star, g.name as genre " +
              "FROM top20_movies m " +
              "INNER JOIN stars_in_movies sm ON sm.movieId = m.id " +
              "INNER JOIN genres_in_movies gm ON gm.movieId = m.id " +
              "INNER JOIN stars s ON s.id = sm.starId " +
              "INNER JOIN genres g ON  g.id = gm.genreId";

      // Perform the query
      ResultSet resultSet = statement.executeQuery(query);

      JsonArray jsonArray = new JsonArray();

      // Iterate through each row of resultSet
      while (resultSet.next()) {
        String movie_id = resultSet.getString("id");
        String movie_title = resultSet.getString("title");
        String movie_year = resultSet.getString("year");
        String movie_director = resultSet.getString("director");
        String movie_rating = resultSet.getString("rating");
        String movie_genre = resultSet.getString("genre");
        String movie_star = resultSet.getString("star");

        // Create a JsonObject based on the data we retrieve from resultSet
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("movie_id", movie_id);
        jsonObject.addProperty("movie_title", movie_title);
        jsonObject.addProperty("movie_year", movie_year);
        jsonObject.addProperty("movie_director", movie_director);
        jsonObject.addProperty("movie_genre", movie_genre);
        jsonObject.addProperty("movie_star", movie_star);
        jsonObject.addProperty("movie_rating", movie_rating);

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