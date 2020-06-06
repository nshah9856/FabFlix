import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.Context;
import javax.naming.InitialContext;
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

/**
 * TODO: Same as movie list except not 3, all
 * (prob single query)
 */
//Declare WebServlet
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/movie")
public class SingleMovieServlet extends HttpServlet {
  private static final long serialVersionUID = 2L;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setContentType("application/json"); // Response mime type
    response.setCharacterEncoding("UTF-8");

    // Retrieve parameter id from url request.
    String id = request.getParameter("id");

    // Output stream to STDOUT
    PrintWriter out = response.getWriter();

    try {
      // Obtain our environment naming context
      Context initContext = new InitialContext();
      Context envContext = (Context) initContext.lookup("java:/comp/env");
      DataSource dataSource = (DataSource) envContext.lookup("jdbc/moviedb");

      // Get a connection from dataSource
      Connection connection = dataSource.getConnection();

      // Construct a query with parameter represented by "?"
      String query = "SELECT m.id, m.title, m.year, m.director, m.price, stars_name as stars_name, " +
          "stars_id as stars_id, gtemp.genres as genres,gtemp.genre_ids as genre_ids, r.rating as" +
          " " +
          "rating from movies m " +
          "LEFT JOIN ratings r on r.movieId = m.id " +
          "INNER JOIN (SELECT gm.movieId, GROUP_CONCAT(g.name order by g.name asc) as genres, " +
          "GROUP_CONCAT(g.id order by g.name asc) as genre_ids " +
          "from genres_in_movies gm " +
          "INNER JOIN genres g ON g.id = gm.genreId GROUP BY movieId) as gtemp ON gtemp.movieId = m.id " +
          "INNER JOIN (select sm.movieId, GROUP_CONCAT(s.id order by s.movieCnt desc, s.name) as stars_id, " +
          "GROUP_CONCAT(s.name order by s.movieCnt desc, s.name) as stars_name from stars_in_movies sm " +
          "INNER JOIN (select s.id, s.name, count(*) as movieCnt from stars_in_movies sm, stars s where sm.starId = s.id group by starId) as s ON s.id = sm.starId " +
          "GROUP BY movieId) as stemp ON stemp.movieId = m.id where m.id = ?";

      // Declare our statement
      PreparedStatement statement = connection.prepareStatement(query);

      // Set the parameter represented by "?" in the query to the movie id we get from url,
      // num 1 indicates the first "?" in the query
      statement.setString(1, id);

      ResultSet resultSet = statement.executeQuery();

      JsonArray jsonArray = new JsonArray();

      // Iterate through each row of resultSet
      while (resultSet.next()) {
        String movie_id = resultSet.getString("id");
        String movie_title = resultSet.getString("title");
        String movie_year = resultSet.getString("year");
        String movie_director = resultSet.getString("director");
        String movie_price = resultSet.getString("price");
        String movie_genres = resultSet.getString("genres");
        String movie_genres_ids = resultSet.getString("genre_ids");
        String movie_stars = resultSet.getString("stars_name");
        String movie_star_id = resultSet.getString("stars_id");
        String movie_ratings = resultSet.getString("rating");

        // Create a JsonObject based on the data we retrieve from resultSet
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("movie_id", movie_id);
        jsonObject.addProperty("movie_title", movie_title);
        jsonObject.addProperty("movie_year", movie_year);
        jsonObject.addProperty("movie_director", movie_director);
        jsonObject.addProperty("movie_price", movie_price);
        jsonObject.addProperty("movie_genres", movie_genres);
        jsonObject.addProperty("movie_genre_ids", movie_genres_ids);
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