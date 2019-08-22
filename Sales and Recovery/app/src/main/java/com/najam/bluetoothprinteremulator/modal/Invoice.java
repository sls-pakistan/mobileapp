package com.najam.bluetoothprinteremulator.modal;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by HP on 11/12/2017.
 */

public class Invoice {
    private String saleId;
    private Date saleDate;
    private String saleDesc;
    private String accId;
    private Double discRate;
    private Double bonusRate;
    private Double cashPaid;
    private ArrayList<InvoiceItem> items;

    public Invoice() {
        items = new ArrayList<InvoiceItem>();
    }

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public String getSaleDesc() {
        return saleDesc;
    }

    public void setSaleDesc(String saleDesc) {
        this.saleDesc = saleDesc;
    }

    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    public Double getDiscRate() {
        if (discRate == null) {
            discRate = new Double(0);
        }
        return discRate;
    }

    public void setDiscRate(Double discRate) {
        this.discRate = discRate;
    }

    public Double getBonusRate() {
        return bonusRate;
    }

    public void setBonusRate(Double bonusRate) {
        this.bonusRate = bonusRate;
    }

    public Double getCashPaid() {
        if (cashPaid == null) {
            cashPaid = new Double(0.0);
        }
        return cashPaid;
    }

    public void setCashPaid(Double cashPaid) {
        this.cashPaid = cashPaid;
    }

    public ArrayList<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<InvoiceItem> items) {
        this.items = items;
    }
}
