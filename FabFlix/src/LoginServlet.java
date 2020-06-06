import com.google.gson.JsonObject;

import javax.annotation.Resource;
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


import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;


@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    public String getServletInfo() {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        if(request.getParameter("mobile") == null ) {
            String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
            // Verify reCAPTCHA
            try {
                System.out.println("VERYFYING.....");
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            } catch (Exception e) {
                System.out.println("IN THE ERROR...");
                // write error message JSON object to output
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("errorMessage", e.getMessage());
                out.write(jsonObject.toString());
                out.close();
                // set response status to 500 (Internal Server Error)
                response.setStatus(200);
                return;
            }
        }
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        JsonObject responseJsonObject = new JsonObject();

        try {
            // Obtain our environment naming context
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/moviedb");

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

            boolean success = false;
            PasswordEncryptor e = new StrongPasswordEncryptor();

           if (resultSet.next()) {
               customer_email = resultSet.getString("email");
               customer_password = resultSet.getString("password");
               customer_id = resultSet.getString("id");
               // use the same encryptor to compare the user input password with encrypted password stored in DB
               System.out.println(password + " = = " + customer_password);
               success = e.checkPassword(password, customer_password);
           }
           if(success == false){
               // Login fail
               responseJsonObject.addProperty("status", "fail");
               responseJsonObject.addProperty("message", "incorrect username/password");
               response.setStatus(200);
               out.write(responseJsonObject.toString());
               out.close();
               connection.close();
               statement.close();
                return;
           }
           if (username.equals(customer_email)) {

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
                    responseJsonObject.addProperty("message", "incorrect username/password");
                }
            }

            out.write(responseJsonObject.toString());
            out.close();
            connection.close();
            statement.close();

        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
            out.close();

        }
    }
}
