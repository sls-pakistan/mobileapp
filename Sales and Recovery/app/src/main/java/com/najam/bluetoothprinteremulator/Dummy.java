package com.najam.bluetoothprinteremulator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nAjam_Hassan on 2/15/2017.
 */

public class Dummy extends Activity {
    ListView listView;
    TextView tv;
    JSONObject obj,jo_inside,jo_inside1, jo_inside2;
    JSONArray m_jArry, m_jArry1, m_jArry2;
    String str, str1;
    ArrayList<String> arr;
    List<String> arr1, address, phn, balance;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        arr = new ArrayList<String>();

        str = getIntent().getStringExtra("zone");
        str1 = getIntent().getStringExtra("subzone");

        arr.add("heelo");


         Toast.makeText(getApplicationContext(), str ,Toast.LENGTH_LONG).show();

        try {
            obj  = new JSONObject(loadJSONFromAsset());
            m_jArry = obj.getJSONArray("zones");
            for (int i = 0; i < m_jArry.length(); i++) {
                jo_inside = m_jArry.getJSONObject(i);
                String s = jo_inside.getString("zoneName");
                if(s.equals(str)){
                    //Toast.makeText(getApplicationContext(), "inside",Toast.LENGTH_LONG).show();
                    m_jArry1 = jo_inside.getJSONArray("subZones");
                    for (int j = 0; j < m_jArry1.length(); j++) {
                        jo_inside1 = m_jArry1.getJSONObject(j);
                        String s1 = jo_inside1.getString("sZoneName");
                        if(s1.equals(str1)){

                            m_jArry2 = jo_inside1.getJSONArray("accounts");
                            for (int z = 0; z < m_jArry2.length(); z++) {
                                jo_inside2 = m_jArry2.getJSONObject(z);
                                arr.add(jo_inside2.getString("accName"));
                                //Toast.makeText(getApplicationContext(), "if",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Else",Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListAdapter adapter = new AdapterList(this,arr);

        tv = (TextView) findViewById(R.id.lttv);
        tv.setText("Sub Zone List");

        listView = (ListView) findViewById(R.id.lv);
        listView.setAdapter(adapter);

    }

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
    }
}

