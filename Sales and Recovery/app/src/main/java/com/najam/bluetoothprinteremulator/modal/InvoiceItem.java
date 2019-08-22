package com.najam.bluetoothprinteremulator.modal;

import java.io.Serializable;

/**
 * Created by HP on 11/12/2017.
 */

public class InvoiceItem implements Serializable {
    private String saleID;
    private String proID;
    private String proDesc;
    private String salDetDesc;
    private Double proPrice;
    private Double priceDisc;
    private Double proQty;

    public String getSaleID() {
        return saleID;
    }

    public void setSaleID(String saleID) {
        this.saleID = saleID;
    }

    public String getProID() {
        return proID;
    }

    public void setProID(String proID) {
        this.proID = proID;
    }

    public String getProDesc() {
        return proDesc;
    }

    public void setProDesc(String proDesc) {
        this.proDesc = proDesc;
    }

    public String getSalDetDesc() {
        return salDetDesc;
    }

    public void setSalDetDesc(String salDetDesc) {
        this.salDetDesc = salDetDesc;
    }

    public Double getProPrice() {
        return proPrice;
    }

    public void setProPrice(Double proPrice) {
        this.proPrice = proPrice;
    }

    public Double getPriceDisc() {
        return priceDisc;
    }

    public void setPriceDisc(Double priceDisc) {
        this.priceDisc = priceDisc;
    }

    public Double getProQty() {
        return proQty;
    }

    public void setProQty(Double proQty) {
        this.proQty = proQty;
    }
}
