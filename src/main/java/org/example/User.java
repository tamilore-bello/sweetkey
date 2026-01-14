package org.example;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class User {
    private String uuid;
    private String username;
    private String password;
    private String email;
    private Date date_joined;

    public User (String u, String p) {
        username = u;
        password = p;
        date_joined = (Date.from(Instant.now()));
        uuid = String.valueOf(UUID.randomUUID());
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
    public String getUuid() {
        return uuid;
    }
    public String getPassword() {
        return password;
    }


}
