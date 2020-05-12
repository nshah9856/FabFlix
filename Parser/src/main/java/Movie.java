import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Movie {

    private String title;

    private int year;

    private String id;

    private String director;

    private String xmlId;

    List<Genre> genres;

    List<Star> stars;

    public Movie(){
        genres = new ArrayList<>();
        stars = new ArrayList<>();
    }

    public Movie(String id, String xmlId, String title, int year, String director) {
        this.title = title;
        this.year = year;
        this.id  = id;
        this.director = director;
        this.xmlId = xmlId;

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

    public String getXmlId() {
        return xmlId;
    }

    public void setXmlId(String id) {
        this.xmlId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void addGenre(Genre g) {
        this.genres.add(g);
    }
    public String getGenres() {
        StringBuffer sb = new StringBuffer();

        Iterator<Genre> it = genres.iterator();
        sb.append('[');
        while (it.hasNext()) {
            sb.append(it.next().toString());
            sb.append(',');
        }
        sb.append(']');
        return sb.toString();
    }
    public List<Genre> getGenreList(){
        return this.genres;
    }

    public String getStars() {
        StringBuffer sb = new StringBuffer();

        Iterator<Star> it = stars.iterator();
        sb.append('[');
        while (it.hasNext()) {
            sb.append(it.next().toString());
            sb.append(',');
        }
        sb.append(']');
        return sb.toString();
    }
    public List<Star> getStarList(){
        return this.stars;
    }


    public void setStars(List<Star> stars) {
        this.stars = stars;
    }

    public void addStar(Star star) {
        this.stars.add(star);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Movie Details - ");
        sb.append("ID:" + getId());
        sb.append(", ");
        sb.append("Title:" + getTitle());
        sb.append(", ");
        sb.append("Director:" + getDirector());
        sb.append(", ");
        sb.append("Year:" + getYear());
        sb.append(", ");
        sb.append("Genres:" + getGenres());
        sb.append(", ");
        sb.append(("Stars:" + getStars()));
        sb.append(".");

        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                append(xmlId).
                append(director).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Movie))
            return false;
        if (obj == this)
            return true;

        Movie rhs = (Movie) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                        append(xmlId, rhs.xmlId).
                        append(director,rhs.director).
                        isEquals();
    }
}