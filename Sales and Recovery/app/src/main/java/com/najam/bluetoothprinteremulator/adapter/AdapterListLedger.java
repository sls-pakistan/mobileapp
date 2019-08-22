package com.najam.bluetoothprinteremulator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.najam.bluetoothprinteremulator.R;
import com.najam.bluetoothprinteremulator.modal.Transaction;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by nAjam_Hassan on 2/28/2017.
 */

public class AdapterListLedger extends BaseAdapter {
    private Context c;
    private ArrayList<Transaction> transactions;
    private ArrayList<Double> balances;


    public AdapterListLedger(Context context, ArrayList<Transaction> transactions) {
        this.c = context;
        this.transactions = transactions;
        this.balances = new ArrayList<Double>();
        Double rblance = new Double(0);
        for (Transaction t: this.transactions) {
            rblance += t.getDebit();
            rblance -= t.getCredit();
            this.balances.add(rblance.doubleValue());
        }
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
            convertView = inflater.inflate(R.layout.list_text_view_ledger, null);
        }
        final AdapterListLedger.ViewHolder holder = new AdapterListLedger.ViewHolder(convertView);
        holder.setData(getItem(position), balances.get(position));

        return convertView;
    }

    public class ViewHolder {
        View view, parentView;
        TextView date, memo, debit, credit, balance;

        ViewHolder(View view) {
            this.view = view;
            initViews();
        }

        private void initViews() {
            date = view.findViewById(R.id.ldate);
            memo = view.findViewById(R.id.lmemo);
            debit = view.findViewById(R.id.ldebit);
            credit = view.findViewById(R.id.lcredit);
            balance = view.findViewById(R.id.lbal);
            parentView = view.findViewById(R.id.parentView);
        }

        private void setData(Transaction transaction, Double rBalance) {
            DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

            date.setText(format.format(transaction.getDate()));
            memo.setText(String.valueOf(transaction.getVouDesc()));
            debit.setText(NumberFormat.getInstance(Locale.US).format(transaction.getDebit()));
            credit.setText(NumberFormat.getInstance(Locale.US).format(transaction.getCredit()));

            if (transaction.getVouTypeID().equalsIgnoreCase("sv") && transaction.getVouID().contains("_")) {
                view.setBackgroundColor(c.getResources().getColor(R.color.invoiceBackground_shade));
                parentView.setBackgroundColor(c.getResources().getColor(R.color.invoiceBackground_shade));
            }
            balance.setText(NumberFormat.getInstance(Locale.US).format(rBalance));
        }
    }
}
