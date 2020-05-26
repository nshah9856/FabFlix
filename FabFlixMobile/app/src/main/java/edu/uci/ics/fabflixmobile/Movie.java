package edu.uci.ics.fabflixmobile;

// title, year, director, the first 3 genres (hyperlink is optional), the first 3 stars (hyperlink is optional)
public class Movie {
    private String name;
    private short year;
    private String director;
    String genres;
    String stars;

    public Movie(String name, short year, String director, String genres, String stars) {
        this.name = name;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
    }

    public String getName() {
        return name;
    }

    public short getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public String getGenres() {
        StringBuilder returnStr = new StringBuilder();
        String[] temp =  this.genres.split(",");
        for (int i = 0; i < (Math.min(temp.length, 3)); i++) {
            returnStr.append(temp[i]).append(", ");
        }
        return returnStr.substring(0, Math.max(returnStr.length() - 2, 0));
    }

    public String getGenresAll() {
        return this.genres;
    }

    public String getStars() {
        StringBuilder returnStr = new StringBuilder();
        String[] temp =  this.stars.split(",");
        for (int i = 0; i < (Math.min(temp.length, 3)); i++) {
            returnStr.append(temp[i]).append(", ");
        }
        return returnStr.substring(0, Math.max(returnStr.length() - 2, 0));
    }

    public String getStarsAll() {
        return this.stars;
    }
}