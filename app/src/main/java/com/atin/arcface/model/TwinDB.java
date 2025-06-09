package com.atin.arcface.model;

public class TwinDB {
    private int id;
    private String personId;
    private String similarPersonId;

    public TwinDB() {
    }

    public TwinDB(int id, String personId, String similarPersonId) {
        this.id = id;
        this.personId = personId;
        this.similarPersonId = similarPersonId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getSimilarPersonId() {
        return similarPersonId;
    }

    public void setSimilarPersonId(String similarPersonId) {
        this.similarPersonId = similarPersonId;
    }
}
