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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

//Declare WebServlet
@WebServlet(name = "AddMovieServlet", urlPatterns = "/_dashboard/api/addMovie")
public class AddMovieServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  // Create a dataSource which registered in web.xml
  @Resource(name = "jdbc/moviedb")
  private DataSource dataSource;

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("application/json"); // Response mime type
    response.setCharacterEncoding("UTF-8");

    String movie_title = request.getParameter("title");
    String movie_year = request.getParameter("year");
    String movie_director = request.getParameter("director");
    String star_name = request.getParameter("star");
    String genre_name = request.getParameter("genre");

    // Output stream to STDOUT
    PrintWriter out = response.getWriter();

    try {
      // Get a connection from dataSource
      Connection connection = dataSource.getConnection();
//      String query = "CALL add_movie(?, ?, ?, ?, ?)";

      // Declare our statement
      //TODO: check CallableStatement
//      PreparedStatement statement = connection.prepareStatement(query);
      CallableStatement statement = connection.prepareCall("{call add_movie(?, ?, ?, ?, ?)}");

      statement.setString(1, movie_title);
      statement.setInt(2, Integer.parseInt(movie_year));
      statement.setString(3, movie_director);
      statement.setString(4, star_name);
      statement.setString(5, genre_name);

      System.out.println(statement);
      // Perform the query
      boolean hadResults = statement.execute();

      JsonObject responseJsonObject = new JsonObject();

      while (hadResults) {
        ResultSet rs = statement.getResultSet();
        while (rs.next()) {
//          System.out.println("CHECKING IF");
//          if (rs.getString("message").startsWith("SUCCESS")){
//            System.out.println("IN IF");
//            responseJsonObject.addProperty("success", true);
//          }
//          else{
//            responseJsonObject.addProperty("failure", true);
//          }
//          System.out.println("AFTER ELSE");
          responseJsonObject.addProperty("message", rs.getString("message"));
        }
        // process result set
        hadResults = statement.getMoreResults();
      }

      // write JSON string to output
      out.write(responseJsonObject.toString());

      // set response status to 200 (OK)
      response.setStatus(200);
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