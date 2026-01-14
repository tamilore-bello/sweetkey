package org.example;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class Commission {

    String id;
    String artist_id;
    String commissioner_handle;
    String platform;
    Date date_ordered;
    Date date_expected;
    String size;
    Double cost;
    String description;
    String reference_link;
    Boolean payment_received;

    public Commission(String artistID, String commissionerHandle, String sizeOf, String referenceLink, Boolean paymentReceived) {
        artist_id = artistID;
        id = (UUID.randomUUID()).toString();
        commissioner_handle = commissionerHandle;
        date_ordered = Date.from(Instant.now());
        size = sizeOf;
        reference_link = referenceLink;
        payment_received = paymentReceived;
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
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    public void setCommissioner_handle(String commissioner_handle) {
        this.commissioner_handle = commissioner_handle;
    }
}

