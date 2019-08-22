package com.najam.bluetoothprinteremulator.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.najam.bluetoothprinteremulator.DataModalComponent;
import com.najam.bluetoothprinteremulator.LedgerActivity;
import com.najam.bluetoothprinteremulator.R;
import com.najam.bluetoothprinteremulator.adapter.NewPaymentsAdapter;
import com.najam.bluetoothprinteremulator.modal.Invoice;
import com.najam.bluetoothprinteremulator.modal.InvoiceItem;
import com.najam.bluetoothprinteremulator.modal.Transaction;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.najam.bluetoothprinteremulator.ui.PrintInvoiceActivity.ACCOUNT_ID;
import static com.najam.bluetoothprinteremulator.ui.PrintInvoiceActivity.INVOICE_ID;

public class ViewPaymentsActivity extends Activity implements AdapterView.OnItemLongClickListener {
    private ListView listView;
    private ArrayList<Transaction> transactions;
    private ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_payments);
        transactions = new ArrayList<Transaction>();
        initViews();
        setData();
    }

    private void setData() {
        try {
            DataModalComponent component = DataModalComponent.getInstance(this);
            for (Invoice i : component.getBaseModal().getNewInvoices()){
                Transaction transaction = new Transaction();
                transaction.setAccId(i.getAccId());

                transaction.setDate(i.getSaleDate());
                transaction.setVouDesc(component.getAccountByAccountID(i.getAccId()).getAccName());
                transaction.setVouID("sv_" + i.getSaleId());
                transaction.setVouTypeID("SV");
                Double totalBalance = 0.0;
                for(InvoiceItem ii : i.getItems()){
                    if(ii.getSaleID()!= null) {
                        Double priceDisc = ii.getPriceDisc();
                        Double price = ii.getProPrice();
                        Double qty = ii.getProQty();
                        Double amt = qty * price * (1 - priceDisc / 100);
                        totalBalance += amt;
                    }
                }
                totalBalance = totalBalance - i.getDiscRate();
                transaction.setDebit((double)Math.round(totalBalance.doubleValue()));
                if (i.getAccId().equals(component.getBaseModal().getDefaults().getCashAccId())){
                    transaction.setCredit((double) Math.round((double)Math.round(totalBalance.doubleValue())));
                }else{
                    transaction.setCredit((double) Math.round(i.getCashPaid().doubleValue()));
                }
                transactions.add(transaction);
            }

            for (Transaction t : component.getBaseModal().getNewReceipts()){
                Transaction transaction = new Transaction();
                transaction.setAccId(t.getAccId());

                transaction.setCredit(t.getCredit());
                transaction.setDate(t.getDate());
                transaction.setDebit(t.getDebit());
                transaction.setVouDesc(component.getAccountByAccountID(t.getAccId()).getAccName());
                transaction.setVouID(t.getVouID());
                transaction.setVouTypeID(t.getVouTypeID());
                transactions.add(transaction);
            }
            Collections.sort(transactions);

            NewPaymentsAdapter newPaymentsAdapter = new NewPaymentsAdapter(this, transactions);
            listView.setAdapter(newPaymentsAdapter);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        listView = findViewById(R.id.payments_lv);
        backIcon = findViewById(R.id.back_icon);
        listView.setOnItemLongClickListener(this);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (transactions.get(i).getVouTypeID().equalsIgnoreCase("sv") && transactions.get(i).getVouID().contains("_")) {
            String invoiceId = transactions.get(i).getVouID().split("_")[1];
            Intent intent = new Intent(ViewPaymentsActivity.this, PrintInvoiceActivity.class);
            intent.putExtra(INVOICE_ID, invoiceId);
            intent.putExtra(ACCOUNT_ID, transactions.get(i).getAccId());
            startActivity(intent);
            return true;
        } else {
            return false;
        }
    }
}
