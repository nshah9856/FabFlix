import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Star {

  private String name;

  private int year;

  private String id;

  private String director;

  List<Movie> movies;

  public Star(){
    movies = new ArrayList<Movie>();
  }

  public Star(String id, String name) {
    this.name = name;
    this.year = year;
    this.id  = id;

  }
  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDirector() {
    return director;
  }

  public void setDirector(String director) {
    this.director = director;
  }


  public void addMovie(Movie m) {
    this.movies.add(m);
  }
  public String getMovies() {
    StringBuffer sb = new StringBuffer();

    Iterator<Movie> it = movies.iterator();
    sb.append('[');
    while (it.hasNext()) {
      sb.append(it.next().toString());
      sb.append(',');
    }
    sb.append(']');
    return sb.toString();
  }
  public List<Movie> getGenreList(){
    return this.movies;
  }


  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("Movie Details - ");
    sb.append("ID:" + getId());
    sb.append(", ");
    sb.append("Name:" + getName());
    sb.append(", ");
    sb.append("Year:" + getYear());
    sb.append(", ");
    sb.append("Movies:" + getMovies());
    sb.append(".");

    return sb.toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            append(name).
            toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Star))
      return false;
    if (obj == this)
      return true;

    Star rhs = (Star) obj;
    return new EqualsBuilder().
            // if deriving: appendSuper(super.equals(obj)).
                    append(name, rhs.name).
                    isEquals();
  }
}
