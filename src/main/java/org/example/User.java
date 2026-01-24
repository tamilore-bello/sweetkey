package org.example;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class User {
    private final String id;
    private String username;
    private String email;
    private final Date date_joined;

    // minimum user constructor
    public User (String u, String e) {
        username = u;
        email = e;
        date_joined = (Date.from(Instant.now()));
        id = String.valueOf(UUID.randomUUID());
    }

    // complete user constructor, used for database operations
    public User (String i, String u, String e, Date dj) {
        username = u;
        date_joined = dj;
        id = i;
        email = e;
    }

    // setters
    public void setEmail(String email) {
        this.email = email;
    }
    public void setUsername(String u) {
        this.username = u;
    }

    // getters
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public Date getDate_joined() {
        return date_joined;
    }
    public String getId() {
        return id;
    }

    // public toString method
    public String toString() {
        return (getUsername()+"\n"+
                "> EMAIL: "+getEmail()+"\n"+
                "> ID: "+getId()+"\n");
    }

}
