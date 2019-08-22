package com.najam.bluetoothprinteremulator.modal;

import java.util.ArrayList;

/**
 * Created by HP on 11/12/2017.
 */

public class Category implements Comparable<Category>{
    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    private String catID;
    private String catName;
    private ArrayList<Product> products;

    public Category(){
        products = new ArrayList<Product>();
    }


    @Override
    public int compareTo(Category o) {
        return this.getCatName().compareTo(o.getCatName());
    }

}
