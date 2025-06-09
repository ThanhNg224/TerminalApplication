package com.atin.arcface.model;

public class EventOfPersonRequest {
    private String personId;
    private String accessDate;

    public EventOfPersonRequest() {
    }

    public EventOfPersonRequest(String personId, String accessDate) {
        this.personId = personId;
        this.accessDate = accessDate;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(String accessDate) {
        this.accessDate = accessDate;
    }
}
