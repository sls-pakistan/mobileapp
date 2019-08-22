package com.najam.bluetoothprinteremulator.modal;

/**
 * Created by HP on 11/12/2017.
 */

public class Product implements Comparable<Product>{
    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
    }

    public String getProID() {
        return proID;
    }

    public void setProID(String proID) {
        this.proID = proID;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public Double getProPrice() {
        return proPrice;
    }

    public void setProPrice(Double proPrice) {
        this.proPrice = proPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    private String catID;
    private String proID;
    private String proName;
    private Double proPrice;
    private String unit;

    @Override
    public int compareTo(Product o) {
        return this.getProName().compareTo(o.getProName());
    }
}
