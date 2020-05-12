import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
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
import java.sql.Statement;

//Declare WebServlet
@WebServlet(name = "AddStarServlet", urlPatterns = "/_dashboard/api/addStar")
public class AddStarServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type
        response.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String year = request.getParameter("year");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection connection = dataSource.getConnection();

            ResultSet resultSet = connection.prepareStatement("select concat(\"nm\",(select max(substring(id, 3)) from stars) + 1) as starID").executeQuery();
            resultSet.next(); // There only should be one value
            System.out.println("NEW ID"  + resultSet.getString("starID"));

            String query = "INSERT INTO stars VALUES(?, ?, ?)";

            // Declare our statement
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, resultSet.getString("starID")); //set the id
            statement.setString(2, name);
            if(year != null)
                statement.setInt(3, Integer.parseInt(year));
            else
                statement.setString(3, null);

            System.out.println("Updatig.. " + statement);
            // Perform the query
            statement.executeUpdate();

            System.out.println("failed?");
            JsonObject responseJsonObject = new JsonObject();

            responseJsonObject.addProperty("success", true);
            responseJsonObject.addProperty("starId",resultSet.getString("starID"));

            // write JSON string to output
            out.write(responseJsonObject.toString());

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