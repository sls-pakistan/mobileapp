package com.najam.bluetoothprinteremulator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import com.najam.bluetoothprinteremulator.modal.SubZone;

/**
 * Created by nAjam uL hAssAn on 04-02-2017.
 */

public class SubZoneListAdapter extends BaseAdapter {
    Context context;
    private ArrayList<SubZone> subZones;

    SubZoneListAdapter(Context context, ArrayList<SubZone> subZones) {
        this.subZones = subZones;
        this.context = context;
    }

    @Override
    public int getCount() {
        return subZones.size();
    }

    @Override
    public SubZone getItem(int i) {
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
        SubZone subZone = getItem(position);
        TextView tv = convertView.findViewById(R.id.ltv);
        tv.setText(subZone.getsZoneName());
        return convertView;
    }
}
