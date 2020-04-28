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

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        JsonObject responseJsonObject = new JsonObject();

        try {
            // Get a connection from dataSource
            Connection connection = dataSource.getConnection();

            String query = "SELECT * FROM customers AS c WHERE c.email = ?";
            String customer_email = "";
            String customer_password = "";
            String customer_id = "";
            // Declare our statement
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            // Perform the query
            ResultSet resultSet = statement.executeQuery();

           if (resultSet.next()) {
               customer_email = resultSet.getString("email");
               customer_password = resultSet.getString("password");
               customer_id = resultSet.getString("id");
           }

            if (username.equals(customer_email) && password.equals(customer_password)) {
                // Login success:

                // set this user into the session
                request.getSession().setAttribute("user", new User(username));

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
                request.getSession().setAttribute("user", customer_id);
            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");

                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                if (!username.equals(customer_email)) {
                    responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
                } else {
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            }

            response.getWriter().write(responseJsonObject.toString());
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
