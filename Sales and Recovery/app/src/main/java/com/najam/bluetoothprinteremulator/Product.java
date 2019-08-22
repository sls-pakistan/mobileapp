package com.najam.bluetoothprinteremulator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.najam.bluetoothprinteremulator.modal.BaseModal;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static com.najam.bluetoothprinteremulator.Catagory.ADD_PRODUCTS;

/**
 * Created by nAjam uL hAssAn on 05-02-2017.
 */

public class Product extends Activity {
    public static final int ADD_INVOICES = 1214;
    BaseModal lj;
    Context c;
    ListView listView;
    String catId, s;
    TextView tv;
    String selectedProduct;
    ArrayList<com.najam.bluetoothprinteremulator.modal.Product> products;
    private boolean isToReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        c = getApplicationContext();
        isToReturn = getIntent().getBooleanExtra("toReturn", false);

        catId = getIntent().getStringExtra("catId");
        try {
            products = DataModalComponent.getInstance(c).getProductsByCatId(catId);
            Collections.sort(products);
        }catch (Exception e){
            products = new ArrayList<com.najam.bluetoothprinteremulator.modal.Product>();
            e.printStackTrace();
        }
        tv = (TextView) findViewById(R.id.lttv);
        tv.setText("Product List");

        listView = (ListView) findViewById(R.id.lv);

        ProductListAdapter adapter = new ProductListAdapter(this, products);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getApplicationContext(),array[i],Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Product.this, AddInvoice.class);
                intent.putExtra("Catagory", catId);
                intent.putExtra("Product", products.get(i).getProName());
                intent.putExtra("Price", products.get(i).getProPrice().toString());
                intent.putExtra("toReturn", isToReturn);
                selectedProduct = products.get(i).getProID();
                startActivityForResult(intent, ADD_INVOICES);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case ADD_INVOICES:
                data.putExtra("proID", selectedProduct);
                setResult(ADD_PRODUCTS, data);
                finish();
                break;
        }
    }
}