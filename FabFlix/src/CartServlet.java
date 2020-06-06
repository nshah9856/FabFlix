import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
import java.util.ArrayList;
import java.util.TreeMap;

@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();

        TreeMap<String,String> cart = ( TreeMap<String,String>) session.getAttribute("cart");

        if(cart == null){
            cart = new TreeMap<>();
        }

        String movie_id = request.getParameter("id");
        String movie_title = request.getParameter("title");
        String movie_price = request.getParameter("price");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Obtain our environment naming context
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/moviedb");

            // Get a connection from dataSource
            Connection connection = dataSource.getConnection();

            synchronized (cart){
                if(movie_id != null && movie_title != null && movie_price != null){
                    String curr = cart.get(movie_id);
                    if(curr == null) {
                        JsonObject responseJsonObject = new JsonObject();
                        responseJsonObject.addProperty("movie_id", movie_id);
                        responseJsonObject.addProperty("movie_title", movie_title);
                        responseJsonObject.addProperty("movie_price", movie_price);
                        responseJsonObject.addProperty("movie_quantity", 1);
                        cart.put(movie_id, responseJsonObject.toString());
                    }
                    else{
                        JsonParser parser = new JsonParser();
                        JsonObject json = (JsonObject) parser. parse(curr);
                        Integer i = json.get("movie_quantity").getAsInt();
                        json.remove("movie_quantity");
                        json.addProperty("movie_quantity", i + 1);
                        cart.replace(movie_id, json.toString());
                    }
                }
            }
            session.setAttribute("cart",cart);
            ArrayList<String> listCart = new ArrayList(cart.values());
            System.out.println("Value" + listCart.toString());
            out.write(listCart.toString());
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();

        TreeMap<String,String> cart = ( TreeMap<String,String>) session.getAttribute("cart");

        if(cart == null){
            cart = new TreeMap<>();
        }

        String movie_id = request.getParameter("id");
        String remove = request.getParameter("remove");
        String movie_quantity = request.getParameter("quantity");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Obtain our environment naming context
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/moviedb");

            // Get a connection from dataSource
            Connection connection = dataSource.getConnection();
            JsonParser parser = new JsonParser();

            synchronized (cart){
                String currItem = cart.get(movie_id);
                if(currItem == null){
                    System.out.println("NOT FOUND!!");
                    connection.close();
                    return;
                }
                JsonObject json = (JsonObject) parser.parse(currItem);
                if(movie_id != null && movie_quantity != null){
                    json.remove("movie_quantity");
                    json.addProperty("movie_quantity", Integer.parseInt(movie_quantity));
                    cart.replace(movie_id, json.toString());
                }
                else if(remove != null){
                    cart.remove(movie_id);
                }
            }
            session.setAttribute("cart",cart);

            response.setStatus(200);

            out.close();
            connection.close();
        } catch (Exception e) {

            // write error message JSON object to output
//            JsonObject jsonObject = new JsonObject();
            System.out.println("errorMessage" + e.getMessage());
//            out.write(jsonObject.toString());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
            out.close();
        }
    }
}
