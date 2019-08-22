package com.najam.bluetoothprinteremulator.modal;

import java.util.ArrayList;

/**
 * Created by HP on 11/12/2017.
 */

public class Account implements Comparable<Account> {
    private String sZoneID;
    private String zoneID;
    private String accID;
    private String accName;
    private String address;
    private String phone;
    private Double balance;
    private Integer rank;
    private ArrayList<Transaction> transactions;
    private ArrayList<Invoice> invoices;

    public Account() {
        transactions = new ArrayList<Transaction>();
        invoices = new ArrayList<Invoice>();
    }

    public String getsZoneID() {
        return sZoneID;
    }

    public void setsZoneID(String sZoneID) {
        this.sZoneID = sZoneID;
    }

    public String getAccID() {
        return accID;
    }

    public void setAccID(String accID) {
        this.accID = accID;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public ArrayList<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(ArrayList<Invoice> invoices) {
        this.invoices = invoices;
    }

    public String getZoneID() {
        return zoneID;
    }

    public void setZoneID(String zoneID) {
        this.zoneID = zoneID;
    }

    @Override
    public int compareTo(Account o) {
        return this.getAccName().compareTo(o.getAccName());
    }
}
