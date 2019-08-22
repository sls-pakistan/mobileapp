package com.najam.bluetoothprinteremulator;
import com.najam.bluetoothprinteremulator.modal.Zone;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ZoneActivity extends Activity {
    Context c;
    ListView listView;
    TextView tv;
    ArrayList<Zone> zones;
    private TextView noContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        c = getApplicationContext();

        zones = new ArrayList<Zone>();
        try {
            zones = DataModalComponent.getInstance(this).getBaseModal().getZones();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ZoneListAdapter adapter = new ZoneListAdapter(this, zones);

        tv = findViewById(R.id.lttv);
        tv.setText(R.string.zone_list);
        listView = findViewById(R.id.lv);
        noContent = findViewById(R.id.no_content);
        if (zones.size() == 0) {
            noContent.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            noContent.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getApplicationContext(),array[i],Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ZoneActivity.this, SubZoneActivity.class);
                intent.putExtra("zone", zones.get(i).getZoneID());
                startActivity(intent);
            }
        });

    }

}
