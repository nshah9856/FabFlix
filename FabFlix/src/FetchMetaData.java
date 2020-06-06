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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;


@WebServlet(name = "FetchMetaData", urlPatterns = "/_dashboard/api/metaData")
public class FetchMetaData extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    public String getServletInfo() {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Output stream to STDOUT

        PrintWriter out = response.getWriter();

        JsonArray jsonArray = new JsonArray();

        try {
            // Obtain our environment naming context
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/moviedb");

            // Get a connection from dataSource
            Connection connection = dataSource.getConnection();

            DatabaseMetaData d = connection.getMetaData();
            ResultSet set = d.getColumns(null, null, "%", "%");

            while(set.next()){
                JsonObject responseJsonObject = new JsonObject();

                String table = set.getString("TABLE_NAME");
                String column = set.getString("COLUMN_NAME");
                String type = set.getString("TYPE_NAME");
                String size = set.getString("COLUMN_SIZE");

                responseJsonObject.addProperty("table", table);
                responseJsonObject.addProperty("column", column);
                responseJsonObject.addProperty("type", type);
                responseJsonObject.addProperty("size", size);

//                System.out.println(table + "  " + column + " "  + type + " " + size);
                jsonArray.add(responseJsonObject);
            }
            response.setStatus(200);
            out.write(jsonArray.toString());
            out.close();
            connection.close();

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
