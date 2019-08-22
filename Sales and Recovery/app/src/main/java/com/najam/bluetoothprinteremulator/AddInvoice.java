package com.najam.bluetoothprinteremulator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.najam.bluetoothprinteremulator.general.Utility;
import com.najam.bluetoothprinteremulator.modal.InvoiceItem;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.najam.bluetoothprinteremulator.InvoiceList.EDIT_ITEM;
import static com.najam.bluetoothprinteremulator.Product.ADD_INVOICES;

/**
 * Created by nAjam_Hassan on 2/16/2017.
 */

public class AddInvoice extends Activity {

    public String finalString = null;
    String cat, pro, rate;
    TextView catPro, ammount, titleText;
    EditText quantity, price, discount;
    Button save, cancel;
    JSONObject jObj;
    JSONArray jArray;
    LoadJson lj;
    SaveFile sf;
    Context c;
    InvoiceItem invoiceItem;
    private boolean toReturnItem = false;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addinvoice);

        c = getApplicationContext();
        lj = new LoadJson();
        sf = new SaveFile(c);

        cat = getIntent().getStringExtra("Catagory");
        pro = getIntent().getStringExtra("Product");
        rate = getIntent().getStringExtra("Price");
        toReturnItem = getIntent().getBooleanExtra("toReturn", false);
        if (getIntent().getExtras() != null)
            invoiceItem = (InvoiceItem) getIntent().getExtras().get("invoiceItem");
        if (invoiceItem != null) {
            isEditMode = true;
        }

        catPro = findViewById(R.id.catpro);
        ammount = findViewById(R.id.aiammount);

        quantity = findViewById(R.id.aiqty);
        price = findViewById(R.id.aiprice);
        discount = findViewById(R.id.aidiscount);

        save = findViewById(R.id.aisave);
        cancel = findViewById(R.id.aicancel);
        titleText = findViewById(R.id.add_invoice_title);

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN){
            quantity.setTextColor(Color.WHITE);
            price.setTextColor(Color.WHITE);
            discount.setTextColor(Color.WHITE);

        }

        if (isEditMode) {
            titleText.setText(getString(R.string.edit_invoice_item));
        } else if (toReturnItem) {
            titleText.setText(getString(R.string.return_invoice_item));
        } else {
            titleText.setText(getString(R.string.add_invoice_item));
        }

        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ammount.setText(String.valueOf(Math.ceil((Utility.getValidDouble(quantity.getText().toString())
                        * Utility.getValidDouble(price.getText().toString()))
                        * (1 - (Utility.getDouble(discount.getText().toString()) / 100)))));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    ammount.setText(String.valueOf(Math.ceil((Utility.getValidDouble(quantity.getText().toString())
                            * Utility.getValidDouble(price.getText().toString()))
                            * (1 - (Utility.getDouble(discount.getText().toString()) / 100)))));
                }
                catch(Exception e){

                    }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ammount.setText(String.valueOf(Math.ceil((Utility.getValidDouble(quantity.getText().toString())
                        * Utility.getValidDouble(price.getText().toString()))
                        * (1 - (Utility.getDouble(discount.getText().toString()) / 100)))));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (!isEditMode) {
            catPro.setText(String.format("%s / %s", cat, pro));
            price.setText(rate);
        } else {
            catPro.setText(invoiceItem.getProDesc());
            price.setText(String.valueOf(invoiceItem.getProPrice()));
            quantity.setText(String.valueOf(invoiceItem.getProQty()));
            discount.setText(String.valueOf(invoiceItem.getPriceDisc()));
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEditMode) {
                    Intent intent = new Intent(AddInvoice.this, Product.class);
                    intent.putExtra("proPrice", price.getText().toString());
                    intent.putExtra("proDesc", pro);
                    intent.putExtra("priceDisc", discount.getText().toString());
                    intent.putExtra("proQty", quantity.getText().toString());
                    intent.putExtra("isToReturn", toReturnItem);
                    setResult(ADD_INVOICES, intent);
                } else {
                    Intent intent = new Intent(AddInvoice.this, Product.class);
                    invoiceItem.setProQty(Utility.getDouble(quantity.getText().toString()));
                    invoiceItem.setPriceDisc(Utility.getDouble(discount.getText().toString()));
                    invoiceItem.setProPrice(Utility.getDouble(price.getText().toString()));
                    intent.putExtra("invoiceItem", invoiceItem);
                    setResult(EDIT_ITEM, intent);
                }
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity.setText(" ");
                discount.setText(" ");
                ammount.setText(" ");
            }
        });
    }
}
