import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//Declare WebServlet
@WebServlet(name = "AutocompleteServlet", urlPatterns = "/api/autocomplete")
public class AutocompleteServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("application/json"); // Response mime type
    response.setCharacterEncoding("UTF-8");

    // Output stream to STDOUT
    PrintWriter out = response.getWriter();

    try {
      // Obtain our environment naming context
      Context initContext = new InitialContext();
      Context envContext = (Context) initContext.lookup("java:/comp/env");
      DataSource dataSource = (DataSource) envContext.lookup("jdbc/moviedb");

      // Get a connection from dataSource
      Connection connection = dataSource.getConnection();

      HttpSession session = request.getSession();

      String title = request.getParameter("query") != null ?
          request.getParameter("query") : "";

      String query = "SELECT id, title FROM movies WHERE match(title) against (? IN BOOLEAN " +
              "MODE) OR ed(?, lower(title)) <= ? LIMIT 10";

      PreparedStatement statement = connection.prepareStatement(query);
      String filter_string = "";
      if (title.length() > 0)
      {
        String [] filters = title.split(" ");
        for (String word : filters)
        {
          filter_string += "+" + word + "* ";
        }
      }
      statement.setString(1, filter_string); //if "", empty set is returned
      statement.setString(2,title.toLowerCase());

      if(title.length() < 4)
        statement.setInt(3, 1);
      else if(title.length() < 6)
        statement.setInt(3, 2);
      else
        statement.setInt(3, 3);

      System.out.println(statement);
      // Perform the query
      ResultSet resultSet = statement.executeQuery();
      JsonArray jsonArray = new JsonArray();

      // Iterate through each row of resultSet
      while (resultSet.next()) {
        String movie_id = resultSet.getString("id");
        String movie_title = resultSet.getString("title");

        // Create a JsonObject based on the data we retrieve from resultSet

        jsonArray.add(generateJsonObject(movie_id, movie_title));
      }

      // write JSON string to output
      out.write(jsonArray.toString());
      session.setAttribute("queryResult", jsonArray.toString());
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
    finally {
      out.close();
    }
  }

  private static JsonObject generateJsonObject(String id, String title) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("value", title);

    JsonObject additionalDataJsonObject = new JsonObject();
    additionalDataJsonObject.addProperty("id", id);

    jsonObject.add("data", additionalDataJsonObject);
    return jsonObject;
  }
}