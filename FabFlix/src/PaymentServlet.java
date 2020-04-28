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

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */

  // Create a dataSource which registered in web.xml
  @Resource(name = "jdbc/moviedb")
  private DataSource dataSource;

  protected void doPost(HttpServletRequest request, //TODO: change to doPost
  HttpServletResponse response) throws ServletException, IOException {
    System.out.println(request);
    String givenId = request.getParameter("id");
    String givenFirst = request.getParameter("firstName");
    String givenLast = request.getParameter("lastName");
    String givenExpiration = request.getParameter("expiration");

    // Output stream to STDOUT
    PrintWriter out = response.getWriter();

    try {
      // Get a connection from dataSource
      Connection connection = dataSource.getConnection();

      String query = "SELECT * FROM creditcards AS c WHERE c.id = ?";
      String id = "";
      String firstName = "";
      String lastName = "";
      String expiration = "";

      // Declare our statement
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setString(1, givenId);
      // Perform the query
      ResultSet resultSet = statement.executeQuery();


      JsonObject jsonObject = new JsonObject();
      while (resultSet.next()) {
        id = resultSet.getString("id");
        firstName = resultSet.getString("firstName");
        lastName = resultSet.getString("lastName");
        expiration = resultSet.getString("expiration");


        jsonObject.addProperty("id", id);
        jsonObject.addProperty("firstName", firstName);
        jsonObject.addProperty("lastName", lastName);
        jsonObject.addProperty("expiration", expiration);


      }

      if (givenFirst.equals(firstName) && givenLast.equals(lastName) &&
          givenId.equals(id) && givenExpiration.equals(expiration)) {


// write JSON string to output


        jsonObject.addProperty("status", "success");
        jsonObject.addProperty("message", "success");

        // set response status to 200 (OK)

      } else {
        // Login fail
        jsonObject.addProperty("status", "fail");

        // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
        if (!givenId.equals(id)) {
          jsonObject.addProperty("message", "Card " + id + " not found on file");
        } else if (!givenFirst.equals(firstName) || !givenLast.equals(lastName)) {
          jsonObject.addProperty("message", "cardholder " + givenFirst + " " + givenLast +
              " not found on file");
        }
        else { //expiration not match
          jsonObject.addProperty("message", "incorrect expiration date");
        }
      }

      response.setStatus(200);
      out.write(jsonObject.toString());
      connection.close();

    } catch (Exception e) {

      // write error message JSON object to output
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("errorMessage", e.getMessage());
      out.write(jsonObject.toString());

      // set response status to 500 (Internal Server Error)
      response.setStatus(500);

    }
  }
}
