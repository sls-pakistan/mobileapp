package com.najam.bluetoothprinteremulator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.najam.bluetoothprinteremulator.modal.Account;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by nAjam uL hAssAn on 05-02-2017.
 */

public class AddAccount extends Activity {
    public static final int ADD_ACCOUNT_KEY = 2121;
    DataModalComponent lj;
    SaveFile sf;
    Context c;
    EditText name, address, phn;
    Button save, cancel;
    String zone, subzone;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_account);

        name = findViewById(R.id.aaname);
        address = findViewById(R.id.aaadd);
        phn = findViewById(R.id.aiqty);
        if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.JELLY_BEAN){
            name.setTextColor(Color.WHITE);
            address.setTextColor(Color.WHITE);
            phn.setTextColor(Color.WHITE);
        }
        save = findViewById(R.id.aasave);
        cancel = findViewById(R.id.aicancel);

        zone = getIntent().getStringExtra("zone");
        subzone = getIntent().getStringExtra("subzone");

        c = getApplicationContext();
        try {
            lj = DataModalComponent.getInstance(this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sf = new SaveFile(c);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save.setEnabled(false);
                Account account = new Account();
                if (name.getText().toString().equals("")) {
                    name.setError("Not a valid name!");
                    return;
                }
                if (address.getText().toString().equals("")) {
                    address.setError("Not a valid address!");
                    return;
                }
                if (phn.getText().toString().equals("") || phn.getText().toString().length() != 11) {
                    phn.setError("Not a valid number!");
                    return;
                }

                account.setAccName(name.getText().toString());
                account.setAddress(address.getText().toString());
                account.setPhone(phn.getText().toString());
                account.setsZoneID(subzone);
                account.setZoneID(zone);
                account.setRank(0);
                account.setBalance(0d);
                int newAccountsSize = lj.getBaseModal().getNewAccounts().size();
                if (newAccountsSize == 0) {
                    account.setAccID("0");
                } else {
                    int newAccID = Integer.valueOf(lj.getBaseModal().getNewAccounts().get(newAccountsSize - 1).getAccID()) + 1;
                    account.setAccID(newAccID + "");
                }
                lj.getBaseModal().getNewAccounts().add(account);
                lj.getAccountsBySubZoneId(zone, subzone).add(account);



                lj.saveModalToFile();

                setResult(ADD_ACCOUNT_KEY);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setText("");
                address.setText("");
                phn.setText("");
                finish();
            }
        });
    }
}
