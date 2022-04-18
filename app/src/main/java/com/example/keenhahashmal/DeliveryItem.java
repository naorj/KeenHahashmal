package com.example.keenhahashmal;

import com.google.firebase.database.Exclude;

public class DeliveryItem {

    private String currentDate,dateOfCreate;
    private String time;
    private double price;
    private String invoiceNumber;
    private String uri;
    private String mKey;
    private String nameOfSupplier;
    private String isPaid;


    public  DeliveryItem(){}

    public DeliveryItem(String currentdate, String time, double price, String invoicenumber, String uri, String isPaid,
                        String dateofcreate,String nameofsupplier){

        this.currentDate = currentdate;
        this.time = time;
        this.price = price;
        this.invoiceNumber = invoicenumber;
        this.uri=uri;
        this.isPaid=isPaid;
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

    public String getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(String isPaid){
        this.isPaid=isPaid;
    }

    public String getNameOfSupplier() {
        return nameOfSupplier;
    }

    public void setNameOfSupplier(String nameOfSupplier) {
        this.nameOfSupplier = nameOfSupplier;
    }

    public String getDateOfCreate() {
        return dateOfCreate;
    }

    public void setDateOfCreate(String dateOfCreate) {
        this.dateOfCreate = dateOfCreate;
    }

}