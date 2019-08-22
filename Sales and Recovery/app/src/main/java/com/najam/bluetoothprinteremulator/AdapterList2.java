package com.najam.bluetoothprinteremulator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nAjam uL hAssAn on 08-02-2017.
 */

public class AdapterList2 extends ArrayAdapter<String> {

    LayoutInflater inflater;
    Context c;
    ArrayList<String> name, price;


    public AdapterList2(Context context, ArrayList<String> name, ArrayList<String> price) {
        super(context, R.layout.list, name);

        this.c = context;
        this.name = name;
        this.price = price;

    }

    public class ViewHolder{
        TextView name, price;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_text_view2,null);
        }

        final ViewHolder holder = new ViewHolder();
        holder.name = (TextView) convertView.findViewById(R.id.tvname);
        holder.price = (TextView) convertView.findViewById(R.id.tvprice);

        holder.name.setText(name.get(position));
        holder.price.setText(price.get(position));

        return convertView;
    }

}
