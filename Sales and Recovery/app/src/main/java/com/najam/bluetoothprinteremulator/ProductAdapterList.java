package com.najam.bluetoothprinteremulator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.najam.bluetoothprinteremulator.modal.*;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by nAjam uL hAssAn on 08-02-2017.
 */

public class ProductAdapterList extends ArrayAdapter<com.najam.bluetoothprinteremulator.modal.Product> {

    LayoutInflater inflater;
    Context c;
    ArrayList<com.najam.bluetoothprinteremulator.modal.Product> products;


    public ProductAdapterList(Context context, ArrayList<com.najam.bluetoothprinteremulator.modal.Product> products) {
        super(context, R.layout.list, products);

        this.c = context;
        this.products = products;
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

        holder.name.setText(products.get(position).getProName());
        holder.price.setText(NumberFormat.getInstance(Locale.US).format(products.get(position).getProPrice().longValue()));
        return convertView;
    }
}
