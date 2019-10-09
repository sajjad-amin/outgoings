package com.sajjadamin.outgoings;

public class BudgetDataList {
    String id, title, description, date, amount;

    public BudgetDataList() {
    }

    public BudgetDataList(String id, String title, String description, String date, String amount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
