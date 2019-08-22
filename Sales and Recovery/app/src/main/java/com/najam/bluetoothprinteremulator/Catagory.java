package com.najam.bluetoothprinteremulator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import com.najam.bluetoothprinteremulator.modal.BaseModal;
import com.najam.bluetoothprinteremulator.modal.Category;

import static com.najam.bluetoothprinteremulator.InvoiceList.ADD_ITEMS;

/**
 * Created by nAjam uL hAssAn on 05-02-2017.
 */

public class Catagory extends Activity {
    public static final int ADD_PRODUCTS = 1213;
    BaseModal lj;
    Context c;
    ListView listView;
    JSONObject obj, obj1;
    TextView tv;
    boolean isToReturn;
    ArrayList<Category> arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        isToReturn = getIntent().getBooleanExtra("toReturn",false);
        try {
            lj = DataModalComponent.getInstance(this.getApplicationContext()).getBaseModal();
            arr = lj.getInventory().getCategories();
            Collections.sort(arr);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ListAdapter adapter = new CategoryListAdapter(this, arr);

        tv = (TextView) findViewById(R.id.lttv);
        tv.setText("Catagory List");

        listView = (ListView) findViewById(R.id.lv);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getApplicationContext(),array[i],Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Catagory.this, Product.class);
                intent.putExtra("catId", arr.get(i).getCatID());
                intent.putExtra("toReturn", isToReturn);
                startActivityForResult(intent, ADD_PRODUCTS);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case ADD_PRODUCTS:
                setResult(ADD_ITEMS, data);
                finish();
                break;
        }
    }
    /*
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("extract.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }*/
}
