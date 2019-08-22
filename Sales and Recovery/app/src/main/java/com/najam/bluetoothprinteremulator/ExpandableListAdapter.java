package com.najam.bluetoothprinteremulator;

/**
 * Created by nAjam_Hassan on 2/15/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.najam.bluetoothprinteremulator.InvoiceList.ACCOUNT_ID_KEY;
import static com.najam.bluetoothprinteremulator.InvoiceList.ACCOUNT_NAME_KEY;
import static com.najam.bluetoothprinteremulator.InvoiceList.SUB_ZONE_ID_KEY;
import static com.najam.bluetoothprinteremulator.InvoiceList.ZONE_ID_KEY;
import com.najam.bluetoothprinteremulator.modal.Account;

public class ExpandableListAdapter extends BaseExpandableListAdapter {


    //public List<String> account, balance, address, phn;
    private List<Account> accountList;
    //List<Integer> rank;
    Button ledger, addInvoice, receivePayment;
    String Zone, SubZone;
    //String accountName, tbalance, add, phnno;
    //int lrank;
    private Context _context;
    private DataModalComponent dmc;


    public ExpandableListAdapter(Context context,ArrayList<Account> accountList, String Zone, String SubZone) {
        this._context = context;
        this.Zone = Zone;
        this.SubZone = SubZone;
        try {
            this.accountList = accountList;
            this.dmc = DataModalComponent.getInstance(context);
        }
        catch (Exception e){
            this.accountList = new ArrayList<Account>();
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.accountList.get(groupPosition);

    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Account acc = (Account)this.getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView address = (TextView) convertView.findViewById(R.id.address);
        TextView phn = (TextView) convertView.findViewById(R.id.phone);

        address.setText(acc.getAddress());
        phn.setText(acc.getPhone());

        ledger = (Button) convertView.findViewById(R.id.ledger);
        addInvoice = (Button) convertView.findViewById(R.id.addInvoice);
        receivePayment = (Button) convertView.findViewById(R.id.receivePayment);

        addInvoice.setTag(groupPosition);

        ledger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent12 = new Intent(_context, LedgerActivity.class);
                intent12.putExtra("Zone", Zone);
                intent12.putExtra("SubZone", SubZone);
                intent12.putExtra("IAccount", acc.getAccID());
                intent12.putExtra("IAddress", acc.getAddress());

                _context.startActivity(intent12);

            }
        });

        addInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) addInvoice.getTag();
                //Toast.makeText(view.getContext(),"Add Invoice",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(_context, InvoiceList.class);
                intent.putExtra(ZONE_ID_KEY, Zone);
                intent.putExtra(SUB_ZONE_ID_KEY, SubZone);
                intent.putExtra(ACCOUNT_ID_KEY, accountList.get(position).getAccID());
                intent.putExtra(ACCOUNT_NAME_KEY, accountList.get(position).getAccName());
                dmc.setInvoiceInProgress(null);

                _context.startActivity(intent);
            }
        });

        receivePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(view.getContext(),"Receive Payment",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(_context, ReceivePaymentActivity.class);

                intent.putExtra("IAccount", acc.getAccID());
                intent.putExtra("Zone", Zone);
                intent.putExtra("SubZone", SubZone);

                _context.startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.accountList.get(groupPosition);
    }


    @Override
    public int getGroupCount() {
        return this.accountList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final Account acc = (Account) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.elll);
        TextView account = (TextView) convertView.findViewById(R.id.account);
        TextView bal = (TextView) convertView.findViewById(R.id.balance);
        account.setTypeface(null, Typeface.BOLD);
        bal.setTypeface(null, Typeface.BOLD);
        account.setText(acc.getAccName());
        bal.setText(java.text.NumberFormat.getInstance(Locale.US).format(Math.ceil(acc.getBalance())));
        switch (acc.getRank()){
            case 1:
                ll.setBackgroundColor(Color.parseColor("#e21c00"));
                break;
            case 2:
                ll.setBackgroundColor(Color.parseColor("#c63800"));
                break;
            case 3:
                ll.setBackgroundColor(Color.parseColor("#aa5500"));
                break;
            case 4:
                ll.setBackgroundColor(Color.parseColor("#8d7100"));
                break;
            case 5:
                ll.setBackgroundColor(Color.parseColor("#718d00"));
                break;
            case 6:
                ll.setBackgroundColor(Color.parseColor("#55aa00"));
                break;
            case 7:
                ll.setBackgroundColor(Color.parseColor("#38c600"));
                break;
            case 8:
                ll.setBackgroundColor(Color.parseColor("#1ce200"));
                break;
            case 9:
                ll.setBackgroundColor(Color.parseColor("#00ff00"));
                break;
            case 10:
                ll.setBackgroundColor(Color.parseColor("#ff0000"));
                break;
            default:
                break;
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}