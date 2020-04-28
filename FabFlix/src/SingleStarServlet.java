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
@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/star")
public class SingleStarServlet extends HttpServlet {
  private static final long serialVersionUID = 3L;

  // Create a dataSource which registered in web.xml
  @Resource(name = "jdbc/moviedb")
  private DataSource dataSource;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setContentType("application/json"); // Response mime type
    response.setCharacterEncoding("UTF-8");

    // Retrieve parameter id from url request.
    String id = request.getParameter("id");

    // Output stream to STDOUT
    PrintWriter out = response.getWriter();

    try {
      // Get a connection from dataSource
      Connection connection = dataSource.getConnection();

      String query = "select sm.starId, s.name as star, s.birthYear, group_concat(m.title order " +
          "by m.year desc, m.title) as movie_titles, " +
          "group_concat(m.id order by m.year desc, m.title) as movie_ids " +
          "from stars_in_movies sm, stars s, movies m " +
          "WHERE sm.starId = s.id and m.id = sm.movieId and sm.starId = ? group by sm.starId";

      // Declare our statement
      PreparedStatement statement = connection.prepareStatement(query);

      // Set the parameter represented by "?" in the query to the movie id we get from url,
      // num 1 indicates the first "?" in the query
      statement.setString(1, id);

      System.out.println(statement);
      // Perform the query
      ResultSet resultSet = statement.executeQuery();

      JsonArray jsonArray = new JsonArray();

      // Iterate through each row of resultSet
      while (resultSet.next()) {
        String star_name = resultSet.getString("star");
        String star_year = resultSet.getString("birthYear");
        String movie_ids = resultSet.getString("movie_ids");
        String movie_titles = resultSet.getString("movie_titles");

        // Create a JsonObject based on the data we retrieve from rs
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("star_name", star_name);
        jsonObject.addProperty("star_year", star_year);
        jsonObject.addProperty("movie_ids", movie_ids);
        jsonObject.addProperty("movie_titles", movie_titles);

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