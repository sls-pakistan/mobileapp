package com.najam.bluetoothprinteremulator.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.najam.bluetoothprinteremulator.DataModalComponent;
import com.najam.bluetoothprinteremulator.LoadJson;
import com.najam.bluetoothprinteremulator.R;
import com.najam.bluetoothprinteremulator.modal.BaseModal;
import com.najam.bluetoothprinteremulator.modal.Invoice;
import com.najam.bluetoothprinteremulator.modal.InvoiceItem;
import com.najam.bluetoothprinteremulator.modal.Transaction;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SummaryActivity extends AppCompatActivity {
    private TextView totalSaleAmount, totalCaseSaleAmount, totalRecoveryAmount, cashInHand;
    private LoadJson loadJson;
    private ArrayList arr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        getSupportActionBar().hide();
        initViews();
    }

    private void initViews() {
        totalSaleAmount = (TextView) findViewById(R.id.total_sale_amount);
        totalCaseSaleAmount = (TextView) findViewById(R.id.total_case_sale_amount);
        totalRecoveryAmount = (TextView) findViewById(R.id.total_recovery_amount);
        cashInHand = (TextView) findViewById(R.id.cash_in_hand);

        getData();
    }

    private void getData() {
        DataModalComponent component = null;
        double TotalCashSale = 0;
        double TotalSale = 0;
        double TotalRecovery = 0;
        double totalCashInHand = 0;
        try {
            component = DataModalComponent.getInstance(this);
//            String data = component.loadJSONFromAsset(this);
            BaseModal baseModal = component.getBaseModal();
            if (baseModal != null) {
                List<Transaction> transactions = baseModal.getNewReceipts();
                for (int i = 0; i < transactions.size(); i++) {
                    if (!transactions.get(i).getVouTypeID().equalsIgnoreCase("sv"))
                        TotalRecovery = TotalRecovery + transactions.get(i).getCredit();
                }
                List<Invoice> invoices = baseModal.getNewInvoices();
                for (int i = 0; i < invoices.size(); i++) {
                    double invoiceSum = 0;
                    List<InvoiceItem> invoiceItems = invoices.get(i).getItems();
                    for (int j = 0; j < invoiceItems.size(); j++) {
                        invoiceSum = invoiceSum + ((invoiceItems.get(j).getProPrice() * invoiceItems.get(j).getProQty())
                                * (1 - (invoiceItems.get(j).getPriceDisc() / 100)));
                    }
                    invoiceSum = invoiceSum - invoices.get(i).getDiscRate();
                    TotalSale = TotalSale + invoiceSum;
                    if (invoices.get(i).getAccId().equals(component.getBaseModal().getDefaults().getCashAccId())){
                        TotalCashSale = TotalCashSale + invoiceSum;
                    }else{
                        TotalCashSale = TotalCashSale + invoices.get(i).getCashPaid();

                    }
                }
            }
            totalCashInHand = TotalRecovery + TotalCashSale;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        totalSaleAmount.setText(String.format(getString(R.string.total_sale_amount), TotalSale));
        totalCaseSaleAmount.setText(String.format(getString(R.string.total_case_sale_amount), TotalCashSale));
        totalRecoveryAmount.setText(String.format(getString(R.string.total_recovery_amount), TotalRecovery));
        cashInHand.setText(String.format(getString(R.string.cash_in_hand), totalCashInHand));
    }
}
