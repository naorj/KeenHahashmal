package com.example.keenhahashmal;

import com.google.firebase.database.Exclude;

public class Item {

    private String currentDate,dateOfCreate;
    private String time;
    private double price;
    private String invoiceNumber;
    private String uri;
    private String mKey;
    private String expenseGroup;
    private String nameOfSupplier;
    private String expenseType;

    public  Item(){}
    public Item(String currentdate, String time, double price, String invoicenumber, String uri, String expensegroup,
                String expensetype,String dateofcreate,String nameofsupplier) {
        this.currentDate = currentdate;
        this.time = time;
        this.price = price;
        this.invoiceNumber = invoicenumber;
        this.uri=uri;
        this.expenseGroup=expensegroup;
        this.expenseType=expensetype;
        this.nameOfSupplier=nameofsupplier;
        this.dateOfCreate=dateofcreate;
    }


    public String getDate() {
        return currentDate;
    }

    public void setDate(String currentdate) {
        this.currentDate = currentdate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoicenumber) {
        this.invoiceNumber = invoicenumber;
    }

    public String getUri() {
        return uri;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }
    @Exclude
    public void setKey(String key) {
        this.mKey = key;
    }

    public String getExpenseGroup() {
        return expenseGroup;
    }

    public void setExpenseGroup(String expensegroup) {
        this.expenseGroup = expensegroup;
    }

    public String getNameOfSupplier() {
        return nameOfSupplier;
    }

    public void setNameOfSupplier(String nameOfSupplier) {
        this.nameOfSupplier = nameOfSupplier;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    public String getDateOfCreate() {
        return dateOfCreate;
    }

    public void setDateOfCreate(String dateOfCreate) {
        this.dateOfCreate = dateOfCreate;
    }
}