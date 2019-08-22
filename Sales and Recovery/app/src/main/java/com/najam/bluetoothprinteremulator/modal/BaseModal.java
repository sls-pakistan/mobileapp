package com.najam.bluetoothprinteremulator.modal;

import java.util.ArrayList;

/**
 * Created by HP on 11/12/2017.
 */

public class BaseModal {
    private Defaults defaults;
    private Inventory inventory;
    private ArrayList<Zone> zones;
    private ArrayList<Account> newAccounts;
    private ArrayList<Invoice> newInvoices;
    private ArrayList<Transaction> newReceipts;

    public BaseModal(){
        defaults = new Defaults();
        inventory=new Inventory();
        zones = new ArrayList<Zone>();
        newAccounts = new ArrayList<Account>();
        newReceipts = new ArrayList<Transaction>();
        newInvoices = new ArrayList<Invoice>();
    }

    public Defaults getDefaults() {
        return defaults;
    }

    public void setDefaults(Defaults defaults) {
        this.defaults = defaults;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public ArrayList<Zone> getZones() {
        return zones;
    }

    public void setZones(ArrayList<Zone> zones) {
        this.zones = zones;
    }

    public ArrayList<Account> getNewAccounts() {
        return newAccounts;
    }

    public void setNewAccounts(ArrayList<Account> newAccounts) {
        this.newAccounts = newAccounts;
    }

    public ArrayList<Invoice> getNewInvoices() {
        return newInvoices;
    }

    public void setNewInvoices(ArrayList<Invoice> newInvoices) {
        this.newInvoices = newInvoices;
    }

    public ArrayList<Transaction> getNewReceipts() {
        return newReceipts;
    }

    public void setNewReceipts(ArrayList<Transaction> newReceipts) {
        this.newReceipts = newReceipts;
    }
}
