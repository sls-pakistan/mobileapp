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

import com.najam.bluetoothprinteremulator.adapter.AdapterListLedger;
import com.najam.bluetoothprinteremulator.modal.Account;
import com.najam.bluetoothprinteremulator.modal.Transaction;
import com.najam.bluetoothprinteremulator.ui.PrintInvoiceActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import static com.najam.bluetoothprinteremulator.ui.PrintInvoiceActivity.ACCOUNT_ID;
import static com.najam.bluetoothprinteremulator.ui.PrintInvoiceActivity.INVOICE_ID;

/**
 * Created by nAjam_Hassan on 2/28/2017.
 */

public class LedgerActivity extends Activity {
    Context c;
    String zone, subZone, accid;
    ListView lLV;
    TextView tname, taddress, tbalance;
    double totalBalance = 0;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ledger);
        c = getApplicationContext();

        zone = getIntent().getStringExtra("Zone");
        subZone = getIntent().getStringExtra("SubZone");
        accid = getIntent().getStringExtra("IAccount");

        try {
            DataModalComponent component = DataModalComponent.getInstance(this);
            this.account = component.getAccountByAccId( zone, subZone, accid);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListAdapter adapter = new AdapterListLedger(this, account.getTransactions());

        tname = findViewById(R.id.lname);
        taddress = findViewById(R.id.laddress);
        tbalance = findViewById(R.id.lbalance);

        tname.setText(this.account.getAccName());
        taddress.setText(this.account.getAddress());
        tbalance.setText(String.valueOf(Math.ceil(this.account.getBalance())));

        lLV = findViewById(R.id.ledgerLV);
        lLV.setAdapter(adapter);

        lLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (account.getTransactions().get(i).getVouTypeID().equalsIgnoreCase("sv") && account.getTransactions().get(i).getVouID().contains("_")) {
                    String invoiceId = account.getTransactions().get(i).getVouID().split("_")[1];
                    Intent intent = new Intent(LedgerActivity.this, PrintInvoiceActivity.class);
                    intent.putExtra(INVOICE_ID, invoiceId);
                    intent.putExtra(ACCOUNT_ID, account.getAccName());
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }
}