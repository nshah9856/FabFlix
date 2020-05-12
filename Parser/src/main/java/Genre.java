import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.*;
import java.util.HashMap;

public class Genre {
//create hashmap private
    private String name;

    private Integer id;
    private boolean wrong;

    private HashMap<String, String> genreMap = new HashMap<String, String>();

    private void loadHashMap() {
        genreMap.put("actn", "Action");
        genreMap.put("advt", "Adventure");
        genreMap.put("avant", "Avant Garde");
        genreMap.put("avga", "Avant Garde");
        genreMap.put("bio", "Biographical");
        genreMap.put("camp", "Camp");
        genreMap.put("cart", "Cartoon");
        genreMap.put("cnr", "Cops and Robbers");
        genreMap.put("comd", "Comedy");
        genreMap.put("disa", "Disaster");
        genreMap.put("docu", "Documentary");
        genreMap.put("dram", "Drama");
        genreMap.put("epic", "Epic");
        genreMap.put("faml", "Family");
        genreMap.put("fant", "Fantasy");
        genreMap.put("hist", "History");
        genreMap.put("horr", "Horror");
        genreMap.put("musical", "Musical");
        genreMap.put("musc", "Musical");
        genreMap.put("myst", "Mystery");
        genreMap.put("noir", "Black");
        genreMap.put("porn", "Pornography");
        genreMap.put("romt", "Romance");
        genreMap.put("scfi", "Sci-Fi");
        genreMap.put("surl", "Surreal");
        genreMap.put("susp", "Thriller");
        genreMap.put("tv", "Tv Show");
        genreMap.put("west", "Western");
        genreMap.put("biop", "Biographical Picture");
        genreMap.put("crim", "Crime");
    }

    public Genre(){
        loadHashMap();
    }

    public Genre(Integer id, String name) {
        this.name = name;
        this.id  = id;
        loadHashMap();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(genreMap.get(name.toLowerCase()) == null){
            this.wrong = true;
            this.name = name;
        }
        else{
            this.wrong = false;
            this.name = genreMap.get(name.toLowerCase());
        }
    }

    public void setWrong(boolean b){
        this.wrong = b;
    }

    public boolean getWrong(){
        return this.wrong;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
//        sb.append("Genre Details - ");
//        sb.append("Name:" + getName());
//        sb.append(", ");
        sb.append("ID:" + getId());
        sb.append("Name:" + getName());
//        sb.append(".");
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
        if (!(obj instanceof Genre))
            return false;
        if (obj == this)
            return true;

        Genre rhs = (Genre) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                        append(name, rhs.name).
                        isEquals();
    }
}
