
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.sql.*;
import java.util.*;

public class Parser extends DefaultHandler {

    List<Movie> movies;

    private String tempVal;

    //to maintain context
    private Movie tempMovie;

    private String tempDirector;

    private Genre tempGenre;

    private Integer initMovieid;

    private Integer initGenreId;

    private HashMap<String,Integer> genreIdMap;

    public static HashMap<Movie,String> movieToDbMovie;


    public Parser() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

        PreparedStatement statement = connection.prepareStatement("select max(substring(id, 3)) as id from movies");
        ResultSet r = statement.executeQuery();
        r.next();
        initMovieid = Integer.parseInt(r.getString("id"));

        r = connection.prepareStatement("SELECT MAX(id) as id FROM genres").executeQuery();
        r.next();
        initGenreId = Integer.parseInt(r.getString("id"));

        genreIdMap = new HashMap<>();
        r = connection.prepareStatement("SELECT id,name FROM genres").executeQuery();
        while(r.next()){
            genreIdMap.put(r.getString("name"), Integer.parseInt(r.getString("id")));
        }

        movieToDbMovie = new HashMap<>();

        movies = new ArrayList<Movie>();
    }

    public void run() throws IOException {
        // THE ORDER OF THIS EXECUTION MATTERS!
        parseDocument();
        writeMovieFile();
        writeGenreFile();
        updateMovies();
        writeGenreInMoviesFile();
//        printData();
    }

    private void updateMovies(){
        Iterator<Movie> it = movies.iterator();
        while(it.hasNext()){
            Iterator<Genre> gt = it.next().getGenreList().iterator();
            while (gt.hasNext()){
                Genre g = gt.next();
                g.setId(genreIdMap.get(g.getName()));
            }
        }
    }

    private void writeMovieFile() throws IOException {
        PrintWriter writer = new PrintWriter("movies.txt", "UTF-8");
        Iterator<Movie> it = movies.iterator();
        while (it.hasNext()) {
//            System.out.println(it.next().toString());
            Movie m =it.next();
            String xmlId = m.getId();
            ++initMovieid;
            m.setId(String.format("tt%07d", initMovieid));
            m.setXmlId(xmlId);
            movieToDbMovie.put(m, m.getId());
            writer.printf("%s,%s,%d,%s,%.2f\n", m.getId(),m.getTitle(),m.getYear(),m.getDirector(),Math.random() * 9 + 1);
        }
        writer.close();

//        LOAD DATA LOCAL INFILE '/Users/nisargshah/Desktop/Spring Quarter/CS122B/cs122b-spring20-team-46/Parser/test.txt' INTO TABLE movies fields terminated by ',';
    }

    private void writeGenreInMoviesFile() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("genres_in_movies.txt", "UTF-8");
        PrintWriter badFile = new PrintWriter("inconsistentGenreInMovies.md", "UTF-8");

        Iterator<Movie> it = movies.iterator();
        while(it.hasNext()){
            Movie m = it.next();
            Iterator<Genre> gt = m.getGenreList().iterator();
            while (gt.hasNext()){
                Genre g = gt.next();
                if(g.getId() != null)
                    writer.printf("%d,%s\n",g.getId(),m.getId());
                else{
                    badFile.printf("- Genre %s not found", g.getName());
                }
            }
        }
        writer.close();
        badFile.close();
    }

    private void writeGenreFile() throws IOException {
        PrintWriter writer = new PrintWriter("genres.txt", "UTF-8");
        Set<Genre> hash_Set = new HashSet<Genre>();
        PrintWriter badFile = new PrintWriter("inconsistentGenres.md", "UTF-8");

        Iterator<Movie> it = movies.iterator();
        while (it.hasNext()) {
            Movie m =it.next();
            Iterator<Genre> gt = m.getGenreList().iterator();
            while(gt.hasNext()){
                Genre g = gt.next();
                if(!g.getWrong()) {
                        hash_Set.add(g); // Gives us all "unique" genres
                }
                else{
                    badFile.printf("- Genre Name: %s\n", g.getName());
                }
            }
        }
        Iterator<Genre> i = hash_Set.iterator();
        while (i.hasNext()) {
            // Add to output only if its not already in the database AKA only create a genre thats not already there.
            Genre g = i.next();
            if(!genreIdMap.containsKey(g.getName())){
                ++initGenreId;
                g.setId(initGenreId);
                genreIdMap.put(g.getName(), g.getId());
                writer.printf("%s,%s\n", g.getId(), g.getName());
            }
        }
        writer.close();
        badFile.close();

//        LOAD DATA LOCAL INFILE '/Users/nisargshah/Desktop/Spring Quarter/CS122B/cs122b-spring20-team-46/Parser/test.txt' INTO TABLE movies fields terminated by ',';
    }
    private void parseDocument() {
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse(new File("stanford-movies/mains243.xml"), this);

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

        System.out.println("No of Movies '" + movies.size() + "'.");

        Iterator<Movie> it = movies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) { // Its a movie
            //create a new instance of employee
            tempMovie = new Movie();
        }
        if (qName.equalsIgnoreCase("cat")) { // Its a genre
            tempGenre = new Genre();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("directorfilms")) { // Movies under this director is done looking at..
            tempDirector = null;
        }
        if (qName.equalsIgnoreCase("dirname")) { // Movies under this director is done looking at..
            tempDirector = tempVal;
        }
        if (qName.equalsIgnoreCase("cat")) {
            if(!tempVal.equalsIgnoreCase("ctxx")){
                String [] splits = tempVal.split("[, ?.@-]+");
                for(String s : splits){
                    tempGenre.setName(s.toLowerCase());
                    tempMovie.addGenre(tempGenre);
                    tempGenre = new Genre();
                }
            }
        }
        if (qName.equalsIgnoreCase("film")) {
            //add it to the list
            tempMovie.setDirector(tempDirector);
            movies.add(tempMovie);
        }
        if (qName.equalsIgnoreCase("fid")) {
            tempMovie.setId(tempVal);
        }
            if (qName.equalsIgnoreCase("t")) {
            //add it to the list
            tempMovie.setTitle(tempVal);
        }
        if (qName.equalsIgnoreCase("year")) {
            //add it to the list
            try{
                tempMovie.setYear(Integer.parseInt(tempVal));
            }
            catch (Exception e){
                tempMovie.setYear(0);
            }
        }


    }

//    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
//        Parser spe = new Parser();
//
//        spe.run();

//        Movie m = new Movie();
//        m.setXmlId("AA13");
//        m.setDirector("Asquith");
//        String movieId = Parser.movieToDbMovie.get(m);
//        System.out.println("Movie id " + movieId);
//    }

}
