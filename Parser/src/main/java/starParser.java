import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.sql.*;
import java.util.*;

public class starParser extends DefaultHandler {

  HashMap<String, Movie> movies;

  HashMap<String, String> startodbId;


//  List<Movie> movies;

  private String tempVal;

  //to maintain context
  private Star tempStar;

  private String tempMovie;

  private String tempDirector;

  private String tempTitle;

  private Integer initStarid;

  private List<Star> newStars;

  private Integer count;

  //look to see if star already exist, add A tag to db
  private HashMap<String,Integer> starIdMap;

  public starParser() throws SQLException, ClassNotFoundException, IllegalAccessException,
    InstantiationException {
    String loginUser = "mytestuser";
    String loginPasswd = "mypassword";
    String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

    Class.forName("com.mysql.jdbc.Driver").newInstance();
    Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

    PreparedStatement statement = connection.prepareStatement("select max(substring(id, 3)) as id from stars");
    System.out.println(statement);
    ResultSet r = statement.executeQuery();
    r.next();
    initStarid = Integer.parseInt(r.getString("id"));
    movies = new HashMap<>();
    count = 0;
    newStars = new ArrayList<>();
    startodbId = new HashMap<>();
    r = connection.prepareStatement("select id,name from stars").executeQuery();
    while(r.next()){
      startodbId.put(r.getString("name"),r.getString("id"));

    }
    System.out.println("FOund star " + startodbId.get("Evelyn Varden"));
    System.out.println("initstar id" + initStarid);
  }

  public void run() throws IOException {
    parseDocument();
    writeStarInMoviesFile();
    writeStarFile();
    printData();

    //updateMovies(); //link starId to movie
  }

  private void writeStarInMoviesFile() throws IOException {
    PrintWriter writer = new PrintWriter("stars_in_movies.txt", "UTF-8");

    Iterator hmIterator = movies.entrySet().iterator();

    while (hmIterator.hasNext()) {
      Map.Entry<String, Movie> it = (Map.Entry<String, Movie>) hmIterator.next();

      Movie m = it.getValue();

      if(m.getId() != null){
        Iterator<Star> st = m.getStarList().iterator();

        while(st.hasNext()){
          Star s = st.next();
//          System.out.println("Star: " + s.getName());

          if(startodbId.get(s.getName()) != null) {
            writer.printf("%s,%s\n", startodbId.get(s.getName()), m.getId());
          }
          else{
            count++;
            ++initStarid;
            s.setId(String.format("nm%07d",initStarid));
            startodbId.put(s.getName(), s.getId());
            newStars.add(s);
            writer.printf("nm%s,%s\n", s.getId(), m.getId());
          }
        }
      }
    }
    writer.close();
  }

  private void writeStarFile() throws FileNotFoundException, UnsupportedEncodingException {
    PrintWriter writer = new PrintWriter("moreStars.txt", "UTF-8");
    Iterator<Star> st = newStars.iterator();
    int c = 0;
    while(st.hasNext()){
      Star s = st.next();
      ++c;
      writer.printf("%s,%s\n",s.getId(),s.getName());
    }
    System.out.println("NEW STARS " + c);
    writer.close();

  }

  private void parseDocument() {
    //get a factory
    SAXParserFactory spf = SAXParserFactory.newInstance();
    try {

      //get a new instance of parser
      SAXParser sp = spf.newSAXParser();

      //parse the file and also register this class for call backs
      sp.parse(new File("stanford-movies/casts124.xml"), this);


    } catch (SAXException se) {
      se.printStackTrace();
    } catch (ParserConfigurationException pce) {
      pce.printStackTrace();
    } catch (IOException ie) {
      ie.printStackTrace();
    }
  }

  /**
   * Iterate through the list and print
   * the contents
   */
  private void printData() {

    System.out.println("No of Stars '" + movies.size() + "'.");

    Iterator hmIterator = movies.entrySet().iterator();

//    while (hmIterator.hasNext()) {
//      Map.Entry<String, Movie> it = (Map.Entry<String, Movie>) hmIterator.next();

//      System.out.println(it.getKey()  + " : " + it.getValue().toString());
//    }

    System.out.println("No of new stars '" + count + "'.");

  }

  //Event Handlers
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    //reset
    tempVal = "";
    if (qName.equalsIgnoreCase("m")) { //going through movies
      tempStar = new Star();
    }
  }

  public void characters(char[] ch, int start, int length) throws SAXException {
    tempVal = new String(ch, start, length);
  }

  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (qName.equalsIgnoreCase("dirfilms")) { // Movies under this director is done looking at..
      tempDirector = null;
    }
    if (qName.equalsIgnoreCase("is")) { // Movies under this director is done looking at..
      tempDirector = tempVal;
    }
    if (qName.equalsIgnoreCase("m")) {
      if(movies.get(tempTitle) != null){
        Movie r = movies.get(tempTitle);
        r.addStar(tempStar);
      }
      else{
        Movie m = new Movie();
        m.setDirector(tempDirector);
        m.setXmlId(tempMovie);
        m.setId(Parser.movieToDbMovie.get(m));
        m.addStar(tempStar);
        m.setTitle(tempTitle);
        movies.put(m.getTitle(), m);
      }
    }

    if (qName.equalsIgnoreCase("t")) { //Movie Id
      tempTitle = tempVal;
    }

      if (qName.equalsIgnoreCase("f")) { //Movie Id
        tempMovie = tempVal;

//      if (Parser.movieToDbMovie.get(m) != null)
//        m.addStar(tempStar);
    }

    if (qName.equalsIgnoreCase("a")) { //Star
      tempStar.setName(tempVal);
    }
  }


//    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
//      starParser spe = new starParser();
//      spe.run();
//    }
}
