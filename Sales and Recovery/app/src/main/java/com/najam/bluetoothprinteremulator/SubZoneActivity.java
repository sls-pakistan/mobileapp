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
import android.widget.Toast;

import com.najam.bluetoothprinteremulator.modal.SubZone;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by nAjam uL hAssAn on 04-02-2017.
 */

public class SubZoneActivity extends Activity {
    Context c;
    ListView listView;
    String zoneId;
    ArrayList<SubZone> subZones;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        c = getApplicationContext();
        zoneId = getIntent().getStringExtra("zone");
        // Toast.makeText(getApplicationContext(), zoneId ,Toast.LENGTH_LONG).show();

        try {
            subZones = DataModalComponent.getInstance(this).getSubZoneListByZoneId(zoneId);
            Collections.sort(subZones);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SubZoneListAdapter adapter = new SubZoneListAdapter(this, subZones);

        tv = findViewById(R.id.lttv);
        tv.setText(R.string.sub_zone_list);

        listView = findViewById(R.id.lv);
        TextView noContent = findViewById(R.id.no_content);
        if (subZones.size() == 0) {
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
                Intent intent = new Intent(SubZoneActivity.this, AccountsActivity.class);
                intent.putExtra("zone", zoneId);
                intent.putExtra("subzone", subZones.get(i).getsZoneID());
                startActivity(intent);
            }
        });
    }
}
