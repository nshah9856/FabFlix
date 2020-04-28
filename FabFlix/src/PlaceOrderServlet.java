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
import java.sql.Statement;

//Declare WebServlet
@WebServlet(name = "PlaceOrderServlet", urlPatterns = "/api/order")
public class PlaceOrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type
        response.setCharacterEncoding("UTF-8");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        String customerId = request.getParameter("customer_id");
        String movieId = request.getParameter("movie_id");
        String movieQuantity = request.getParameter("movie_quantity");

        if(customerId == null || movieId == null || movieQuantity == null){
            System.out.println("ERROR IN PLACING ORDER!");
            return;
        }

        try {
            // Get a connection from dataSource
            Connection connection = dataSource.getConnection();

            String query = "INSERT INTO sales VALUES(NULL,?,?, CURRENT_DATE,?);";

            // Declare our statement
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, customerId);
            statement.setString(2,movieId);
            statement.setInt(3,Integer.parseInt(movieQuantity));

            // Perform the insert
            statement.executeUpdate();

            // Get the insert results
            ResultSet resultSet = connection.createStatement().executeQuery("select * from sales where id=LAST_INSERT_ID()");

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of resultSet
            while (resultSet.next()) {
                String sale_id = resultSet.getString("id");
                String sale_date = resultSet.getString("saleDate");
                String movie_id = resultSet.getString("movieId");
                String movie_quantity = resultSet.getString("quantity");

                // Create a JsonObject based on the data we retrieve from resultSet
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("sale_id", sale_id);
                jsonObject.addProperty("sale_date", sale_date);
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_quantity", movie_quantity);

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

//