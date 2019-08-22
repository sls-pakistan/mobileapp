package com.najam.bluetoothprinteremulator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nAjam uL hAssAn on 04-02-2017.
 */

public class AdapterList extends BaseAdapter {
    Context context;
    private ArrayList<String> subZones;

    AdapterList(Context context, ArrayList<String> zones) {
        this.subZones = zones;
        this.context = context;
    }

    @Override
    public int getCount() {
        return subZones.size();
    }

    @Override
    public String getItem(int i) {
        return subZones.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_text_view, parent, false);
        }
        String singleCity = getItem(position);
        TextView tv = convertView.findViewById(R.id.ltv);
        tv.setText(singleCity);
        return convertView;
    }
}
