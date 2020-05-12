import java.io.IOException;
import java.sql.SQLException;

public class Main {
  public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
    Parser spe = new Parser();
    spe.run();
    starParser starpe = new starParser();
    starpe.run();

//        Movie m = new Movie();
//        m.setXmlId("AA13");
//        m.setDirector("Asquith");
//        String movieId = Parser.movieToDbMovie.get(m);
//        System.out.println("Movie id " + movieId);
  }
}
