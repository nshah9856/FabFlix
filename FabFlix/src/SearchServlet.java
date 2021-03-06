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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//Declare WebServlet
@WebServlet(name = "SearchServlet", urlPatterns = "/api/search")
public class SearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type
        response.setCharacterEncoding("UTF-8");
//        System.out.println("ALL THE BEGIN");
        // Output stream to STDOUT
        long startTimeTS = System.nanoTime();
        long elapsedTimeTJ = -1;


        String genre_search = request.getParameter("genre_search") != null ?
                request.getParameter("genre_search") : "%";

        String title_search = request.getParameter("title_search") != null
                ? request.getParameter("title_search") : "%";

        String filter_search = request.getParameter("filter_search") != null ?
                request.getParameter("filter_search") : "false";

        String title = request.getParameter("title") != null ? request.getParameter("title") : "%";

        String year = request.getParameter("year") != null ? request.getParameter("year") :
                "%";

        String director = request.getParameter("director") != null ? request.getParameter("director") : "%";

        String star = request.getParameter("star") != null ? request.getParameter("star") : "%";

        String title_sort = request.getParameter("title_sort");
        String rating_sort = request.getParameter("rating_sort");
        String limit = request.getParameter("limit");
        String offset = request.getParameter("offset");
        String rating_first = request.getParameter("rating_first");
        String single_page = request.getParameter("single_page");


        String query = "SELECT m.id, m.title, m.year, m.director, m.price, stars_name as " +
                "stars_name, stars_id as  stars_id, gtemp.genres as genres, r.rating as rating from movies m " +
                "LEFT JOIN ratings r on r.movieId = m.id INNER JOIN (" +
                "SELECT gm.movieId, GROUP_CONCAT(g.name order by g.name asc) as genres from genres_in_movies gm " +
                "INNER JOIN genres g ON g.id = gm.genreId where g.id like ? GROUP BY movieId) as " +
                "gtemp ON gtemp.movieId = m.id" +
                " INNER JOIN (select sm.movieId, GROUP_CONCAT(s.id order by s.movieCnt desc, s.name) as stars_id, " +
                "GROUP_CONCAT(s.name order by s.movieCnt desc, s.name) as stars_name from stars_in_movies sm " +
                "INNER JOIN (select s.id, s.name, count(*) as movieCnt from stars_in_movies sm, stars s where sm.starId = s.id " +
                "group by starId) as s ON s.id = sm.starId where s.name like ? GROUP BY " +
                "movieId) as stemp ON stemp.movieId = m.id where ^ " +
                "AND year like ? AND director like ?";

        if (filter_search.equals("true"))
        {
            query = query.replace("^", "match(title) against (? IN BOOLEAN MODE)" +
                    " OR ed('" + title.toLowerCase() +
                    "', lower(title)) <= " +
                    (title.length() < 3 ? 0 : title.length() < 4 ? 1 : title.length() < 6 ? 2 : 3)) + " ";
        } else {
            query = query.replace("^", "title " + ((title_search.equals("%")) ? "LIKE" :
                    "REGEXP") + "? ");
        }
        if(rating_first != null){
            if(rating_sort != null) {
                query += " order by r.rating " + rating_sort + ", m.title ";
                if (title_sort != null) {
                    if (title_sort.equals("desc")) {
                        query += "desc ";
                    } else {
                        query += "asc";
                    }
                }
            }
        }
        else{
            if(title_sort != null){
                query += " order by m.title " + title_sort + ", r.rating ";
                if(rating_sort != null){
                    if(rating_sort.equals("desc")){
                        query += "desc ";
                    }
                    else{
                        query += "asc";
                    }
                }
            }
        }
        if (limit != null) {
            query += " limit " + limit;
        }

        if (offset != null) {
            query += " offset " + offset;
        }


        PrintWriter out = response.getWriter();

        try {
            // Obtain our environment naming context
            long startTimeTJ = System.nanoTime();

            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/moviedb");

            // Get a connection from dataSource
            Connection connection = dataSource.getConnection();
//            System.out.println("CONNECTED");

            HttpSession session = request.getSession();

//            System.out.println("STARTING SEARCH");
            if(single_page != null){ // Request is coming from a single star / movie page so reload from cache.
//                System.out.println("Request made from single page");
                String jsonObject = (String)session.getAttribute("queryResult");
                if(jsonObject != null){
//                    System.out.println("Found a cached search");
                    out.write(jsonObject);
                    response.setStatus(200);
                    out.close();
                    connection.close();
                    return;
                }
            }


//            System.out.println("CREATING QUERY");



//            System.out.println("PARAMETERING QUERY");
            // Declare our statement
            PreparedStatement statement = connection.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the movie id we get from url,
            // num 1 indicates the first "?" in the query
            if(genre_search != null && genre_search.equals("true")){
                statement.setInt(1, Integer.parseInt(request.getParameter("genre")));
            } else {
                statement.setString(1, "%");
            }

            if(star.equals("*") || star.equals("%")) {
                statement.setString(2, "%");
            }
            else {
                statement.setString(2, "%" + star.toLowerCase() + "%");
            }
//            System.out.println("filter search: " + filter_search + " title: " + title);
            if (filter_search.equals("true") && !title.equals("%"))
            {
              String [] filters = title.split(" ");
              String filter_string = "";
              for (String word : filters)
              {
                filter_string += "+" + word + "* ";
              }
//              System.out.println("SETTING FILTER STRING TO " + filter_string);
              statement.setString(3, filter_string);
            } else {
              if (title_search.equals("%")) {
                if (title.equals("%")) {
                  statement.setString(3, "%");
                } else {
                  statement.setString(3, "%" + title + "%");
                }
//                System.out.println("TITLE SEARCH NOT GIVEN");
              } else {
                if (title.equals("*")) {
                  statement.setString(3, "^[^0-9A-Za-z]");
                } else {
                  statement.setString(3, "^[" + title.toLowerCase() + title.toUpperCase() + "]");
                }
//                System.out.println("TITLE SEARCH GIVEN");
              }
            }


            if(director.equals("%")) {
                statement.setString(5, "%");
            }
            else {
                statement.setString(5, "%" + director.toLowerCase() + "%");
            }
            statement.setString(4, year);

//            System.out.println(statement.toString());
            ResultSet resultSet = statement.executeQuery();
            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of resultSet
            while (resultSet.next()) {
                String movie_id = resultSet.getString("id");
                String movie_title = resultSet.getString("title");
                String movie_year = resultSet.getString("year");
                String movie_director = resultSet.getString("director");
                String movie_price = resultSet.getString("price");
                String movie_rating = resultSet.getString("rating");
                String movie_genres = resultSet.getString("genres");
                String movie_stars_name = resultSet.getString("stars_name");
                String movie_stars_id = resultSet.getString("stars_id");

                // Create a JsonObject based on the data we retrieve from resultSet
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_price", movie_price);
                jsonObject.addProperty("movie_genres", movie_genres);
                jsonObject.addProperty("movie_stars_name", movie_stars_name);
                jsonObject.addProperty("movie_stars_id", movie_stars_id);
                jsonObject.addProperty("movie_rating", movie_rating);

                jsonArray.add(jsonObject);
            }

            // write JSON string to output
            out.write(jsonArray.toString());
            session.setAttribute("queryResult", jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
            resultSet.close();
            statement.close();
            connection.close();
            long endTimeTJ = System.nanoTime();

            elapsedTimeTJ = endTimeTJ - startTimeTJ;
        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        finally{
            out.close();
        }

        long endTimeTS = System.nanoTime();


        long elapsedTimeTS = endTimeTS - startTimeTS; // elapsed time in nano seconds. Note: print the values in nano seconds


        System.out.println("Output is at " + getServletContext().getRealPath("/") );
        File file = new File( getServletContext().getRealPath("/") + "log.txt");
	if(file.createNewFile()){
        FileWriter myWriter = new FileWriter(getServletContext().getRealPath("/") + file.getName());
        myWriter.write("TS : " + elapsedTimeTS + ", TJ : " + elapsedTimeTJ + "\n");
        myWriter.close();
	}
	else{
        FileWriter myWriter = new FileWriter(getServletContext().getRealPath("/") + file.getName(),true);
        myWriter.write("TS : " + elapsedTimeTS + ", TJ : " + elapsedTimeTJ + "\n");
        myWriter.close();

	}

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
