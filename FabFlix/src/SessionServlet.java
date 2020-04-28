import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
import java.util.ArrayList;
import java.util.TreeMap;

@WebServlet(name = "SessionServlet", urlPatterns = "/api/sessions")
public class SessionServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();

        String cutomer_id = (String) session.getAttribute("user");
        TreeMap<String,String> cart = ( TreeMap<String,String>) session.getAttribute("cart");

        if(cart == null){
            cart = new TreeMap<>();
        }

        String customer_info = request.getParameter("customer_id");
        String cart_info = request.getParameter("cart");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        JsonObject json = new JsonObject();
        try {
            // Get a connection from dataSource
            Connection connection = dataSource.getConnection();
            if(cart_info != null) {
                ArrayList<String> listCart;
                synchronized (cart){
                    listCart = new ArrayList(cart.values());

                    json.addProperty("cart", listCart.toString());
                }
            }

            if(customer_info != null){
                json.addProperty("customer_id",cutomer_id);
            }

            out.write(json.toString());

            response.setStatus(200);

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
