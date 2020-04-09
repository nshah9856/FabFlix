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
@WebServlet(name = "SingleStarServlet", urlPatterns = "/star")
public class SingleStarServlet extends HttpServlet {
  private static final long serialVersionUID = 3L;

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

      String query = "SELECT m.*, s.name, g.name FROM movies m " +
          "INNER JOIN stars_in_movies sm ON sm.movieId = m.id " +
          "INNER JOIN genres_in_movies gm ON gm.movieId = m.id " +
          "INNER JOIN stars s ON s.id = sm.starId " +
          "INNER JOIN genres g ON  g.id = gm.genreId " +
          "WHERE s.id = ?";

      // Declare our statement
      PreparedStatement statement = connection.prepareStatement(query);

      // Set the parameter represented by "?" in the query to the movie id we get from url,
      // num 1 indicates the first "?" in the query
      statement.setString(1, id);

      // Perform the query
      ResultSet resultSet = statement.executeQuery(query);

      JsonArray jsonArray = new JsonArray();

      // Iterate through each row of resultSet
      while (resultSet.next()) {
        String star_id = resultSet.getString("id");
        String star_name = resultSet.getString("name");
        String star_year = resultSet.getString("birthYear");
        String movie_id = resultSet.getString("id");

        // Create a JsonObject based on the data we retrieve from rs
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("star_id", star_id);
        jsonObject.addProperty("star_name", star_name);
        jsonObject.addProperty("star_dob", star_year);
        jsonObject.addProperty("movie_id", movie_id);

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