package com.najam.bluetoothprinteremulator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.najam.bluetoothprinteremulator.modal.Product;

import java.util.ArrayList;

/**
 * Created by nAjam uL hAssAn on 08-02-2017.
 */

public class ProductListAdapter extends ArrayAdapter<Product> {

    LayoutInflater inflater;
    Context c;
    ArrayList<Product> products;

    public ProductListAdapter(Context context, ArrayList<Product> products) {
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

        holder.name.setText(this.products.get(position).getProName());
        holder.price.setText(this.products.get(position).getProPrice().toString());
        return convertView;
    }

}
