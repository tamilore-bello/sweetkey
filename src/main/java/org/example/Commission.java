package org.example;
import java.text.NumberFormat;
import java.time.Instant;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

public class Commission {
    private String id;
    private String artist_id;
    private String commissioner_handle;
    private String platform;
    private Date date_ordered;
    private Date date_expected;
    private String size;
    private Double cost;
    private String description;
    private String reference_link;
    private Boolean payment_received;
    private String status;

    // minimum constructor (used for CLI)
    public Commission(String artistID, String commissionerHandle, String sizeOf, String referenceLink, Boolean paymentReceived) {
        artist_id = artistID;
        id = (UUID.randomUUID()).toString();
        commissioner_handle = commissionerHandle;
        platform = ".";
        date_ordered = Date.from(Instant.now());
        date_expected = Date.from(date_ordered.toInstant().plus(7, ChronoUnit.DAYS)); // set default date_expected to be exactly one week
        size = sizeOf;
        cost = 0.0;
        description = ".";
        reference_link = referenceLink;
        payment_received = paymentReceived;
        status = "unfinished";
    }

    // complete constructor, used for uploading to database operations
    public Commission(String artist_id, String commissioner_handle, String platform, Date date_ordered, Date date_expected, String size, Double cost, String description, String reference_link, Boolean payment_received, String status) {
        id = (UUID.randomUUID()).toString();
        this.artist_id = artist_id;
        this.commissioner_handle = commissioner_handle;
        this.platform = platform;
        this.date_ordered = date_ordered;
        this.date_expected = date_expected;
        this.size = size;
        this.cost = cost;
        this.description = description;
        this.reference_link = reference_link;
        this.payment_received = payment_received;
        this.status = status;
    }

    // complete constructor, used for fetching from database operations
    public Commission(String id, String artist_id, String commissioner_handle, String platform, Date date_ordered, Date date_expected, String size, Double cost, String description, String reference_link, Boolean payment_received, String status) {
        this.id = id;
        this.artist_id = artist_id;
        this.commissioner_handle = commissioner_handle;
        this.platform = platform;
        this.date_ordered = date_ordered;
        this.date_expected = date_expected;
        this.size = size;
        this.cost = cost;
        this.description = description;
        this.reference_link = reference_link;
        this.payment_received = payment_received;
        this.status = status;
    }

    // getters
    public String getReference_link() {
        return reference_link;
    }
    public String getDescription() {
        return description;
    }
    public Double getCost() {
        return cost;
    }
    public String getSize() {
        return size;
    }
    public Date getDate_expected() {
        return date_expected;
    }
    public Date getDate_ordered() {
        return date_ordered;
    }
    public String getPlatform() {
        return platform;
    }
    public String getCommissioner_handle() {
        return commissioner_handle;
    }
    public String getArtist_id() {
        return artist_id;
    }
    public String getId() {
        return id;
    }
    public Boolean getPaymentReceived() {
        return payment_received;
    }
    public String getStatus() { return status; }

    // setters
    public void setReference_link(String reference_link) {
        this.reference_link = reference_link;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public void setDate_expected(Date date_expected) {
        this.date_expected = date_expected;
    }
    public void setDate_ordered(Date date_ordered) {
        this.date_ordered = date_ordered;
    }
    public void setPlatform(String platform) {this.platform = platform;}
    public void setCommissioner_handle(String commissioner_handle) {
        this.commissioner_handle = commissioner_handle;
    }
    public void setStatus(String status) {this.status = status;}

    // public toString method
    public String toString() {
        return ("> COMMISSION ID: "+getId()+"\n"+
                "> ARTIST ID: "+getArtist_id()+"\n"+
                "> COMMISSIONED BY: "+getCommissioner_handle()+"\n"+
                "  ON PLATFORM: "+getPlatform()+"\n"+
                "> DATE ORDERED: "+getDate_ordered()+"\n"+
                "> DATE EXPECTED: "+getDate_expected()+"\n"+
                "> SIZE: "+getSize()+"\n"+
                "> COST: "+NumberFormat.getCurrencyInstance().format(getCost())+"\n"+
                "> STATUS: "+status+"\n"
        );
    }
}

