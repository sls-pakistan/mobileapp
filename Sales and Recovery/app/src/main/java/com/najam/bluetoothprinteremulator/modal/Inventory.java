package com.najam.bluetoothprinteremulator.modal;

import java.util.ArrayList;

/**
 * Created by HP on 11/12/2017.
 */

public class Inventory {
    private ArrayList<Category> categories;
    public Inventory(){
        categories = new ArrayList<Category>();
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }
}
