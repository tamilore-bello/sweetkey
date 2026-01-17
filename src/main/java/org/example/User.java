package org.example;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class User {
    private String id;
    private String username;
    private String password;
    private String email;
    private Date date_joined;

    public User (String u, String p) {
        username = u;
        password = p;
        date_joined = (Date.from(Instant.now()));
        id = String.valueOf(UUID.randomUUID());
    }

    public User (String i, String u, String p, String e, Date dj) {
        username = u;
        password = p;
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
    public String getPassword() {
        return password;
    }

    public String toString() {
        return (getUsername()+"\n"+
                "> EMAIL: "+getEmail()+"\n"+
                "> ID: "+getId()+"\n");
    }

}
