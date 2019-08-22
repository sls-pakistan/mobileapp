package com.najam.bluetoothprinteremulator.modal;

import java.util.Date;

/**
 * Created by HP on 11/12/2017.
 */

public class Transaction implements Comparable<Transaction>{
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getVouTypeID() {
        return vouTypeID;
    }

    public void setVouTypeID(String vouTypeID) {
        this.vouTypeID = vouTypeID;
    }

    public String getVouID() {
        return vouID;
    }

    public void setVouID(String vouID) {
        this.vouID = vouID;
    }

    public String getVouDesc() {
        return vouDesc;
    }

    public void setVouDesc(String vouDesc) {
        this.vouDesc = vouDesc;
    }

    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    public Double getDebit() {
        return debit;
    }

    public void setDebit(Double debit) {
        this.debit = debit;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    private Date date;
    private String vouTypeID;
    private String vouID;
    private String vouDesc;
    private String accId;
    private Double debit;
    private Double credit;

    @Override
    public int compareTo(Transaction o) {
        return this.getDate().compareTo(o.getDate());
    }

}
