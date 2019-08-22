package com.najam.bluetoothprinteremulator.modal;

/**
 * Created by HP on 11/12/2017.
 */

public class Defaults {
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getCashAccId() {
        return cashAccId;
    }

    public void setCashAccId(String cashAccId) {
        this.cashAccId = cashAccId;
    }

    private Company company;
    private String cashAccId;

    public Defaults(){
        company = new Company();
    }
}
