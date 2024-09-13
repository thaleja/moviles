package com.example.appsqilteproducts;

public class Product {
    // Se crearon los atributos
    private String reference;
    private String description;
    private int price;
    private int reftype;

    // Sigue las propiedades


    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getReftype() {
        return reftype;
    }

    public void setReftype(int reftype) {
        this.reftype = reftype;
    }
}
