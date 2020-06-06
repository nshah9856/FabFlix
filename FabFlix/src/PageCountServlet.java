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
@WebServlet(name = "PageCountServlet", urlPatterns = "/api/pagecount")
public class PageCountServlet extends HttpServlet {
  private static final long serialVersionUID = 3L;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("application/json"); // Response mime type
    response.setCharacterEncoding("UTF-8");

    // Output stream to STDOUT
    PrintWriter out = response.getWriter();
    JsonObject responseJsonObject = new JsonObject();

    try {
      // Obtain our environment naming context
      Context initContext = new InitialContext();
      Context envContext = (Context) initContext.lookup("java:/comp/env");
      DataSource dataSource = (DataSource) envContext.lookup("jdbc/moviedb");

      // Get a connection from dataSource
      Connection connection = dataSource.getConnection();
      HttpSession session = request.getSession();

      String savedGenreSearch = (String)session.getAttribute("genre_search");
      String savedGenre = (String)session.getAttribute("genre");
      String savedTitleSearch = (String)session.getAttribute("title_search");
      String savedTitle = (String)session.getAttribute("title");
      String savedYear = (String)session.getAttribute("year");
      String savedDirector = (String)session.getAttribute("director");
      String savedStar = (String)session.getAttribute("star");

      String filter_search = request.getParameter("filter_search");
      String genre_search = request.getParameter("genre_search");
      String genre = request.getParameter("genre");
      String title_search = request.getParameter("title_search");
      String title = request.getParameter("title");
      String year = request.getParameter("year");
      String director = request.getParameter("director");
      String star = request.getParameter("star");

      if(
          ((savedGenreSearch != null && genre_search != null && genre.equals(savedGenre)) || (savedGenreSearch == null && genre_search == null)) && // genre check
          ((savedTitleSearch != null && title_search != null && savedTitle.equals(title_search)) || (savedTitleSearch == null && title_search == null)) && // only title check
          ((savedTitle != null && title != null && title.equals(savedTitle)) || (savedTitle == null && title == null)) && // title check
          ((savedYear != null && year != null && year.equals(savedYear)) || (savedYear == null && year == null)) &&  // year check
          ((savedDirector != null && director != null && director.equals(savedDirector)) || (savedDirector == null && director == null)) &&  // director check
          ((savedStar != null && star != null && star.equals(savedStar)) || (savedStar == null && star == null)) // star check
      ) {
        String rows = (String) session.getAttribute("totalRows");
        if (rows != null) {
          System.out.println("Found a cached pageCount");
          out.write(rows);
          response.setStatus(200);
          out.close();
          connection.close();
          return;
        }
      }

      String query = "SELECT COUNT(*) as title_count FROM movies m  " +
          "LEFT JOIN ratings r on r.movieId = m.id " +
          "INNER JOIN (SELECT gm.movieId, GROUP_CONCAT(g.name) as genres from " +
          "genres_in_movies gm ";

      if(genre_search != null && genre_search.equals("true")){
        query += "INNER JOIN genres g ON g.id = gm.genreId where g" +
            ".id = ? GROUP BY " +
            "movieId) as gtemp ON gtemp.movieId = m.id ";
      }
      else{
        query += "INNER JOIN genres g ON g.id = gm.genreId GROUP BY " +
            "movieId) as gtemp ON gtemp.movieId = m.id ";
      }
      query += "INNER JOIN (select sm.movieId, GROUP_CONCAT(s.id) as stars_id, GROUP_CONCAT(s.name) as stars_name " +
          "from stars_in_movies sm ";

      if(star != null){
        query += "INNER JOIN stars s ON s.id = sm.starId " + "where s.name like '%" + star + "%' " +
            "GROUP BY movieId) as stemp ON stemp.movieId = m.id ";
      }
      else{
        query += "INNER JOIN stars s ON s.id = sm.starId " +
            "GROUP BY movieId) as stemp ON stemp.movieId = m.id ";
      }

      if(filter_search != null){
        query += "where match(title) against (? IN BOOLEAN MODE)";

      }
      else{
        if(title_search != null && title_search.equals("true")){
          query += "where m.title REGEXP ?";
        }
        else{                 // This is an advanced search!
          int p = 0;
          if(title != null){
            p++;
            query += "where m.title like '%" + title + "%'";
          }
          if(year != null){
            if(p > 0) {
              query += " AND ";
              query += "m.year = " + year;
            }
            else{
              query += "where m.year = " + year;
            }
            p++;
          }
          if(director != null){
            if(p > 0){
              query += " AND ";
              query += "m.director like '%" + director + "%'";
            }
            else{
              query += "where m.director like '%" + director + "%'";
            }
            p++;
          }
      }


      }
      System.out.println("Query" + query);

      // Declare our statement
      PreparedStatement statement = connection.prepareStatement(query);

      // Set the parameter represented by "?" in the query to the movie id we get from url,
      // num 1 indicates the first "?" in the query
      if(genre_search != null && genre_search.equals("true")){
        statement.setInt(1, Integer.parseInt(genre));
      }
      else if(filter_search != null){
        String [] filters = title.split(" ");
        String filter_string = "";
        for (String word : filters)
        {
          filter_string += "+" + word + "* ";
        }
        statement.setString(1, filter_string);
      }
      else if(title_search != null && title_search.equals("true")){
        if(title.equals("*")) {
          statement.setString(1, "^[^0-9A-Za-z]");
        }
        else{
          statement.setString(1, "^[" + title.toLowerCase() + title.toUpperCase() + "]");
        }
      }

      System.out.println(statement.toString());
      ResultSet resultSet = statement.executeQuery(); //what is returned

      // Iterate through each row of resultSet
      while (resultSet.next()) {
        String title_count = resultSet.getString("title_count");
        System.out.println("title_count " + title_count);
        // Create a JsonObject based on the data we retrieve from resultSet
        responseJsonObject.addProperty("title_count", title_count);
      }
      // write JSON string to output
      out.write(responseJsonObject.toString());
      session.setAttribute("genre_search", genre_search);
      session.setAttribute("genre", genre);
      session.setAttribute("title_search", title_search);
      session.setAttribute("title", title);
      session.setAttribute("year", year);
      session.setAttribute("director", director);
      session.setAttribute("star", star);
      session.setAttribute("totalRows",responseJsonObject.toString());

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
  protected void doPost(HttpServletRequest request,
                        HttpServletResponse response) throws ServletException, IOException {
    doGet(request,response);
  }
}

/**
 * JSON will only return total # of rows
 * front-end (fetch page count); know # rows, and know current limit; pg_cnt / limit (round up)
 * is # pages
 * loop range & create buttons
 * each tiem click button adds 'limit/offset' to query
 */