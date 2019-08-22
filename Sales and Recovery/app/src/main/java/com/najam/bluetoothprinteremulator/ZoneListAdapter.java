package com.najam.bluetoothprinteremulator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import com.najam.bluetoothprinteremulator.modal.Zone;

/**
 * Created by nAjam uL hAssAn on 04-02-2017.
 */

public class ZoneListAdapter extends BaseAdapter {
    Context context;
    private ArrayList<Zone> zones;

    ZoneListAdapter(Context context, ArrayList<Zone> zones) {
        this.zones = zones;
        this.context = context;
    }

    @Override
    public int getCount() {
        return zones.size();
    }

    @Override
    public Zone getItem(int i) {
        return zones.get(i);
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
        Zone zone = getItem(position);
        TextView tv = convertView.findViewById(R.id.ltv);
        tv.setText(zone.getZoneName());
        return convertView;
    }
}
