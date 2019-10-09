package com.sajjadamin.outgoings;

public class DataList {
    String id, date, amount, description;

    public DataList() {
    }

    public DataList(String id, String date, String amount, String description) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public void setId(String id) { this.id = id; }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
