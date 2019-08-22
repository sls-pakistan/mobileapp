package com.najam.bluetoothprinteremulator.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.najam.bluetoothprinteremulator.DataModalComponent;
import com.najam.bluetoothprinteremulator.R;
import com.najam.bluetoothprinteremulator.modal.Invoice;
import com.najam.bluetoothprinteremulator.modal.Transaction;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by nAjam_Hassan on 2/28/2017.
 */

public class NewPaymentsAdapter extends BaseAdapter {
    private Context c;
    private ArrayList<Transaction> transactions;



    public NewPaymentsAdapter(Context context, ArrayList<Transaction> transactions) {
        this.c = context;
        this.transactions = transactions;
    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Transaction getItem(int i) {
        return transactions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.new_payment_adater, null);
        }
        final NewPaymentsAdapter.ViewHolder holder = new NewPaymentsAdapter.ViewHolder(convertView);
        holder.setData(getItem(position));

        return convertView;
    }

    public class ViewHolder {
        View view, parentView;
        TextView accountTitle, dateTitle, memoTitle, invoiceAmountTitle, paymentTitle;

        ViewHolder(View view) {
            this.view = view;
            initViews();
        }

        private void initViews() {
            dateTitle = view.findViewById(R.id.date_title);
            memoTitle = view.findViewById(R.id.memo_title);
            accountTitle = view.findViewById(R.id.account_title);
            invoiceAmountTitle = view.findViewById(R.id.invoice_amount_title);
            paymentTitle = view.findViewById(R.id.payment_title);
            parentView = view.findViewById(R.id.parentView);
        }

        private void setData(Transaction transaction) {
            DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

            dateTitle.setText(format.format(transaction.getDate()));
            memoTitle.setText(String.valueOf(transaction.getVouDesc()));
            accountTitle.setText(transaction.getAccId());
            paymentTitle.setText(String.valueOf(transaction.getCredit()));
            invoiceAmountTitle.setText(String.valueOf(Math.round(0.0)));

            if (transaction.getVouTypeID().equalsIgnoreCase("sv")) {
                try {
                    DataModalComponent component = DataModalComponent.getInstance(c);
                    Invoice invoice = component.getInvoiceById(transaction.getVouID().split("_")[1]);
                    double netAmount = 0d;
                    for (int i = 0; i < invoice.getItems().size(); i++) {
                        if(invoice.getItems().get(i).getSaleID() != null) {
                            Double priceDisc = invoice.getItems().get(i).getPriceDisc();
                            Double price = invoice.getItems().get(i).getProPrice();
                            Double qty = invoice.getItems().get(i).getProQty();
                            Double amt = qty * price * (1 - priceDisc / 100);
                            netAmount += amt;
                        }
                    }
                    netAmount = netAmount - invoice.getDiscRate();
                    invoiceAmountTitle.setText(String.valueOf(Math.round(netAmount)));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (transaction.getVouTypeID().equalsIgnoreCase("sv") && transaction.getVouID().contains("_")) {
                view.setBackgroundColor(c.getResources().getColor(R.color.invoiceBackground_shade));
                parentView.setBackgroundColor(c.getResources().getColor(R.color.invoiceBackground_shade));
            }
            else{
                view.setBackgroundColor(Color.TRANSPARENT);
                parentView.setBackgroundColor(Color.TRANSPARENT);

            }
        }
    }
}
