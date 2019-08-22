package com.najam.bluetoothprinteremulator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.najam.bluetoothprinteremulator.modal.Account;
import com.najam.bluetoothprinteremulator.modal.BaseModal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.najam.bluetoothprinteremulator.AddAccount.ADD_ACCOUNT_KEY;

/**
 * Created by nAjam_Hassan on 2/15/2017.
 */

public class AccountsActivity extends Activity {
    Context c;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    String zoneId, subZoneId;
    ArrayList<Account> accounts;
    TextView noData;
    ImageView add;
    DataModalComponent component;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        c = getApplicationContext();

        add = findViewById(R.id.accBtn);
        noData = findViewById(R.id.no_data);

        zoneId = getIntent().getStringExtra("zone");
        subZoneId = getIntent().getStringExtra("subzone");

        try {
            component = DataModalComponent.getInstance(this);
            this.accounts = component.getAccountsBySubZoneId(zoneId, subZoneId);
            Collections.sort(this.accounts);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Toast.makeText(getApplicationContext(), zoneId ,Toast.LENGTH_LONG).show();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.gc();
                Intent intent = new Intent(AccountsActivity.this, AddAccount.class);
                intent.putExtra("zone", zoneId);
                intent.putExtra("subzone", subZoneId);
                startActivityForResult(intent, ADD_ACCOUNT_KEY);
            }
        });

        expListView = findViewById(R.id.elv);
        listAdapter = new com.najam.bluetoothprinteremulator.ExpandableListAdapter(this, accounts, zoneId, subZoneId);
        // setting list adapter
        expListView.setAdapter(listAdapter);
        if (listAdapter.getGroupCount() == 0) {
            noData.setVisibility(View.VISIBLE);
            expListView.setVisibility(View.GONE);
        } else {
            noData.setVisibility(View.GONE);
            expListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case ADD_ACCOUNT_KEY:
                finish();
                break;
        }
    }
}