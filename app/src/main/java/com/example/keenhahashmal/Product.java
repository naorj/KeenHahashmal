package com.example.keenhahashmal;

public class Product {

    private String dateOfCreate;
    private double price;
    private String name, description;
    private String mKey;
    private String supplier;

    public Product(){}

    public Product(String name, double price, String description, String dateofcreate, String supplier) {
        this.dateOfCreate=dateofcreate;
        this.price = price;
        this.name = name;
        this.description = description;
        this.supplier=supplier;
    }

    public String getDateOfCreate() { return dateOfCreate; }

    public void setDateOfCreate(String dateOfCreate) { this.dateOfCreate = dateOfCreate; }

    public double getPrice() { return price; }

    public void setPrice(double price) { this.price = price; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getmKey() { return mKey; }

    public void setmKey(String mKey) { this.mKey = mKey; }

    public String getSupplier() { return supplier; }

    public void setSupplier(String supplier) { this.supplier = supplier; }

    @Override
    public String toString() {
        return this.name+", "+String.valueOf(this.price);   // ADDED
    }
}