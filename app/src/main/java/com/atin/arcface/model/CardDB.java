package com.atin.arcface.model;


public class CardDB {
    private String cardId;
    private String personId;
    private String cardNo;

    public CardDB() {
    }

    public CardDB(String cardId, String personId, String cardNo) {
        this.cardId = cardId;
        this.personId = personId;
        this.cardNo = cardNo;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }
}
