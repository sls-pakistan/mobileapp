package com.najam.bluetoothprinteremulator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.najam.bluetoothprinteremulator.modal.Category;

import java.util.ArrayList;

/**
 * Created by nAjam uL hAssAn on 04-02-2017.
 */

public class CategoryListAdapter extends BaseAdapter {
    Context context;
    private ArrayList<Category> categories;

    CategoryListAdapter(Context context, ArrayList<Category> categories) {
        this.categories = categories;
        this.context = context;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Category getItem(int i) {
        return categories.get(i);
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
        Category category = getItem(position);
        TextView tv = convertView.findViewById(R.id.ltv);
        tv.setText(category.getCatName());
        return convertView;
    }
}
