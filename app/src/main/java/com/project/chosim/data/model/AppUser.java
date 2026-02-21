package com.project.chosim.data.model;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AppUser {
    @DocumentId
    private String documentId = null;

    private String email = null;
    private String name = null;
    private int point = 0;
    private Map<String, String> friends = new HashMap<>();

    @ServerTimestamp
    private Date timestamp = null;


    public AppUser() {
    }

    public AppUser(FirebaseUser user) {
        this.email = user.getEmail();
        this.name = user.getDisplayName();
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public Map<String, String> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, String> friends) {
        this.friends = friends;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
