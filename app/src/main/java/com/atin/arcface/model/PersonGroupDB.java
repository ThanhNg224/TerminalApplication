package com.atin.arcface.model;


public class PersonGroupDB {

    private int id;

    private String personId;

    private int groupId;

    public PersonGroupDB() {
    }

    public PersonGroupDB(int id, String personId, int groupId) {
        this.id = id;
        this.personId = personId;
        this.groupId = groupId;
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

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
