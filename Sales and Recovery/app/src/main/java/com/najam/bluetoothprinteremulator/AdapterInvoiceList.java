package com.najam.bluetoothprinteremulator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.najam.bluetoothprinteremulator.modal.InvoiceItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by najam on 14-05-2017.
 */

public class AdapterInvoiceList extends ArrayAdapter<InvoiceItem> {
    Context c;
    private LayoutInflater inflater;
    private ArrayList<InvoiceItem> invoiceItems;
    private AdapterEditOptionListener editOptionListener;

    AdapterInvoiceList(Context context, ArrayList<InvoiceItem> invoiceItems, AdapterEditOptionListener editOptionListener) {
        super(context, R.layout.list, invoiceItems);
        this.invoiceItems = invoiceItems;
        this.editOptionListener = editOptionListener;
        this.c = context;
    }

    public AdapterInvoiceList(Context context, ArrayList<InvoiceItem> invoiceItems) {
        super(context, R.layout.list, invoiceItems);
        this.invoiceItems = invoiceItems;
        this.c = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_text_view_invoice, null);
        }

        final AdapterInvoiceList.ViewHolder holder = new AdapterInvoiceList.ViewHolder();

        holder.Id = convertView.findViewById(R.id.ltviId);
        holder.Desc = convertView.findViewById(R.id.ltviDesc);
        holder.Price = convertView.findViewById(R.id.ltviPrice);
        holder.Discount = convertView.findViewById(R.id.ltviDiscount);
        holder.Quantity = convertView.findViewById(R.id.ltviQuantity);
        holder.Amount = convertView.findViewById(R.id.ltviAmmount);
        holder.divider = convertView.findViewById(R.id.divider);
        holder.deleteItem = convertView.findViewById(R.id.delete_item);
        holder.editItem = convertView.findViewById(R.id.edit_item);


        if (position == 0) {
            holder.Id.setText("Code");
            holder.Desc.setText("Product");
            holder.Price.setText("Price");
            holder.Discount.setText("Disc");
            holder.Quantity.setText("Quantity");
            holder.Amount.setText("Amount");
            holder.divider.setVisibility(View.VISIBLE);
        } else {
            holder.Id.setText(invoiceItems.get(position).getProID());
            holder.Desc.setText(invoiceItems.get(position).getProDesc());
            holder.Price.setText(NumberFormat.getInstance(Locale.US).format(invoiceItems.get(position).getProPrice().intValue()));
            holder.Discount.setText(NumberFormat.getInstance(Locale.US).format(invoiceItems.get(position).getPriceDisc().intValue()));
            holder.Quantity.setText(NumberFormat.getInstance(Locale.US).format(invoiceItems.get(position).getProQty().intValue()));
            Double amount = invoiceItems.get(position).getProQty() * invoiceItems.get(position).getProPrice() * (1 - invoiceItems.get(position).getPriceDisc() / 100);
            holder.Amount.setText(NumberFormat.getInstance(Locale.US).format(Math.ceil(amount)));
            holder.divider.setVisibility(View.GONE);
        }
        if (editOptionListener != null) {
            holder.deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editOptionListener.OnDelete(position);
                }
            });
            holder.editItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editOptionListener.OnEdit(position);
                }
            });
        }
        return convertView;
    }

    public interface AdapterEditOptionListener {
        void OnDelete(int position);

        void OnEdit(int position);
    }

    public class ViewHolder {
        TextView Id, Desc, Price, Discount, Quantity, Amount;
        ImageView deleteItem, editItem;
        View divider;
    }
}
