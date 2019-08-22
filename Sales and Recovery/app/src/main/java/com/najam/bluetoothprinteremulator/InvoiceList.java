package com.najam.bluetoothprinteremulator;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.najam.bluetoothprinteremulator.general.Utility;
import com.najam.bluetoothprinteremulator.modal.Account;
import com.najam.bluetoothprinteremulator.modal.Invoice;
import com.najam.bluetoothprinteremulator.modal.InvoiceItem;
import com.najam.bluetoothprinteremulator.modal.Transaction;
import com.najam.bluetoothprinteremulator.print.helper.BluetoothService;
import com.najam.bluetoothprinteremulator.print.helper.DeviceListActivity;
import com.najam.bluetoothprinteremulator.print.sdk.Command;
import com.najam.bluetoothprinteremulator.print.sdk.PrinterCommand;

import org.json.JSONException;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by najam on 13-05-2017.
 */

public class InvoiceList extends Activity implements AdapterInvoiceList.AdapterEditOptionListener {
    public static final String ZONE_ID_KEY = "zoneId";
    public static final String SUB_ZONE_ID_KEY = "subZoneId";
    public static final String ACCOUNT_ID_KEY = "accId";
    public static final String ACCOUNT_NAME_KEY = "accname";

    public static final int ADD_ITEMS = 1212;
    public static final int EDIT_ITEM = 1213;
    public static final int REQUEST_CONNECT_DEVICE = 1214;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_CONNECTION_LOST = 6;
    public static final int MESSAGE_UNABLE_CONNECT = 7;
    private static final int REQUEST_ENABLE_BT = 1215;
    // Key names received from the BluetoothService Handler
    public static String DEVICE_NAME = "device_name";
    public static String TOAST = "toast";
    private String zoneId, subZoneId, accId, accName;
    private Account account;
    private Invoice invoice;
    private TextView name, balance, tBalance, saleId, netAmmount, newBalance, sdfe;
    private EditText etDiscount, etPaymentReceived;
    private Button addInvoce, btnCancel, btnSave, returnItem;
    private View paymentReceivedView, newBalanceView;
    private CheckBox cbPrint;
    private ListView listView;
    private Context c;
    private DataModalComponent lj;
    private List<Integer> visibleCheckBoxes;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean isBluetoothAvailable;
    private boolean isPrinterConnected;
    private String mConnectedDeviceName;
    private BluetoothService mService;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //TODO: Handle different types of messages
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Toast.makeText(InvoiceList.this, getString(R.string.title_connected_to) + " " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                            isPrinterConnected = true;
                            printReceipt();
                            //printReceipt();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Toast.makeText(InvoiceList.this, getString(R.string.title_connecting), Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            Toast.makeText(InvoiceList.this, getString(R.string.title_not_connected), Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    break;
                case MESSAGE_READ:
                    break;
                case MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Utility.saveBluetoothDevice(mConnectedDeviceName, InvoiceList.this);
                    Toast.makeText(InvoiceList.this, "Connected to $mConnectedDeviceName", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(InvoiceList.this, msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_CONNECTION_LOST:
                    Toast.makeText(InvoiceList.this, "Device connection was lost", Toast.LENGTH_SHORT).show();
                    isPrinterConnected = false;
                    break;
                case MESSAGE_UNABLE_CONNECT:
                    Toast.makeText(InvoiceList.this, "Unable to connect device", Toast.LENGTH_SHORT).show();
                    Utility.saveBluetoothDevice("", InvoiceList.this);
                    startDeviceListActivity();
                    break;
            }
        }
    };

    private void setCalculations() {
        try {
            if (etPaymentReceived.getText().toString().equals("")) {
                etPaymentReceived.setText("0");
            }
            if (etDiscount.getText().toString().equals("")) {
                etDiscount.setText("0");
            }

            Double itemsSum = (double) 0;
            Double overallDisc = Double.valueOf(etDiscount.getText().toString());
            Double amountReceived = Double.valueOf(etPaymentReceived.getText().toString());
            Double netAmount;

            for (int i = 1; i < invoice.getItems().size(); i++) {
                Double priceDisc = invoice.getItems().get(i).getPriceDisc();
                Double price = invoice.getItems().get(i).getProPrice();
                Double qty = invoice.getItems().get(i).getProQty();
                Double amt = Math.ceil(qty * price * (1 - priceDisc / 100));
                itemsSum += amt;
            }
            tBalance.setText(NumberFormat.getInstance(Locale.US).format(Math.ceil(itemsSum)));

            netAmount = itemsSum - overallDisc;
            netAmmount.setText(NumberFormat.getInstance(Locale.US).format(Math.ceil(netAmount)));
            if (account != null)
                newBalance.setText(NumberFormat.getInstance(Locale.US).format(Math.ceil(netAmount - amountReceived + account.getBalance())));

            invoice.setDiscRate(overallDisc);
            invoice.setCashPaid(amountReceived);
            if (account == null) {
                invoice.setCashPaid(netAmount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_list);

        visibleCheckBoxes = new ArrayList<>();

        try {
            lj = DataModalComponent.getInstance(this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        c = getApplicationContext();

        zoneId = getIntent().getStringExtra(ZONE_ID_KEY);
        subZoneId = getIntent().getStringExtra(SUB_ZONE_ID_KEY);
        accId = getIntent().getStringExtra(ACCOUNT_ID_KEY);
        accName = getIntent().getStringExtra(ACCOUNT_NAME_KEY);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            isBluetoothAvailable = false;
            Toast.makeText(this, getString(R.string.error_bluetooth_not_available), Toast.LENGTH_LONG).show();
        } else {
            isBluetoothAvailable = true;
        }

        if (zoneId != null && subZoneId != null)
            account = lj.getAccountByAccId(zoneId, subZoneId, accId);
        invoice = lj.getInvoiceInProgress();
        invoice.setSaleId(lj.getMaxInvId());
        invoice.setAccId(accId);
        invoice.setSaleDate(new Date());
        invoice.setSaleDesc("Added by Application");
        if (invoice.getItems().size() == 0) {
            invoice.getItems().add(new InvoiceItem());
        }
        name = findViewById(R.id.ilName);
        balance = findViewById(R.id.ilBalance);
        tBalance = findViewById(R.id.iltbalance);

        saleId = findViewById(R.id.ilId);
        netAmmount = findViewById(R.id.iltNetAmmount);
        newBalance = findViewById(R.id.iltNewBalance);
        cbPrint = findViewById(R.id.cbPrint);

        etDiscount = findViewById(R.id.iltdiscount);
        etPaymentReceived = findViewById(R.id.iltPaymentRecieved);
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN){
            etDiscount.setTextColor(Color.WHITE);
            etPaymentReceived.setTextColor(Color.WHITE);
        }
        addInvoce = findViewById(R.id.ilAddNew);
        returnItem = findViewById(R.id.ilReturnNew);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        paymentReceivedView = findViewById(R.id.payment_received_view);
        newBalanceView = findViewById(R.id.new_balance_view);
        sdfe = findViewById(R.id.sdfe);
        name.setText(accName);

        if (account != null) {
            balance.setText(NumberFormat.getInstance(Locale.US).format(account.getBalance().longValue()));
        } else {
            paymentReceivedView.setVisibility(View.GONE);
            newBalanceView.setVisibility(View.GONE);
            balance.setVisibility(View.GONE);
            sdfe.setVisibility(View.GONE);
        }
        tBalance.setText(balance.getText().toString());
        saleId.setText(invoice.getSaleId());

        etPaymentReceived.setText(NumberFormat.getInstance(Locale.US).format(invoice.getCashPaid()));
        etDiscount.setText(NumberFormat.getInstance(Locale.US).format(invoice.getDiscRate()));


        listView = findViewById(R.id.illv);
        setItemsAdapter();

        setCalculations();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        etDiscount.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                setCalculations();
            }
        });

        etPaymentReceived.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                setCalculations();
            }
        });

        addInvoce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InvoiceList.this, Catagory.class);
                intent.putExtra("toReturn", false);
                startActivityForResult(intent, ADD_ITEMS);
            }
        });
        returnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InvoiceList.this, Catagory.class);
                intent.putExtra("toReturn", true);
                startActivityForResult(intent, ADD_ITEMS);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSave.setEnabled(false);
                if (invoice.getItems().size() > 0) {
                    if (account == null) {
                        invoice.getItems().remove(0);
                        lj.getBaseModal().getNewInvoices().add(invoice);
                        lj.setInvoiceInProgress(new Invoice());
                    } else {
                        invoice.getItems().remove(0);
                        account.setBalance(Utility.getValidDouble(newBalance.getText().toString().replace(",", "")));
                        account.getInvoices().add(invoice);
                        lj.getBaseModal().getNewInvoices().add(invoice);
                        Transaction transaction = new Transaction();
                        transaction.setVouID("sv_" + invoice.getSaleId());
                        transaction.setAccId(accId);
                        transaction.setVouTypeID("SV");
                        transaction.setVouDesc("Sale Invoice #" + invoice.getSaleId());
                        transaction.setDate(invoice.getSaleDate());
                        transaction.setDebit(Utility.getValidDouble(netAmmount.getText().toString().replace(",", "")));
                        transaction.setCredit(0.0);
                        //Don't add in new transactions
                        //lj.getBaseModal().getNewReceipts().add(transaction);
                        if (zoneId != null && subZoneId != null)
                            lj.updateTransactions(accId, zoneId, subZoneId, transaction);
                        if (Utility.getDouble(etPaymentReceived.getText().toString()) != 0) {
                            Transaction creditTransaction = new Transaction();
                            creditTransaction.setCredit(Utility.getValidDouble(etPaymentReceived.getText().toString().replace(",", "")));
                            creditTransaction.setDebit(0.0);
                            creditTransaction.setVouID("sv_" + invoice.getSaleId());
                            creditTransaction.setAccId(accId);
                            creditTransaction.setVouTypeID("SV");
                            creditTransaction.setVouDesc("Sale Invoice #" + invoice.getSaleId());
                            creditTransaction.setDate(invoice.getSaleDate());
                            if (zoneId != null && subZoneId != null)
                                lj.updateTransactions(accId, zoneId, subZoneId, creditTransaction);
                            //Don't add invoice transaction in new transactions
                            //lj.getBaseModal().getNewReceipts().add(creditTransaction);
                        }
                        lj.setInvoiceInProgress(new Invoice());
                    }
                    lj.saveModalToFile();
                    if (cbPrint.isChecked()) {
                        printInvoice();
                    } else {
                        finish();
                    }
                } else {
                    Utility.AlertDialogMessage(InvoiceList.this, "Please add at leaset one item to add Invoice");
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void printInvoice() {
        if (mBluetoothAdapter != null) {
            if (!isPrinterConnected) {
                String address = Utility.getBluetoothDevice();
                if (!address.equals("")) {
                    attemptDeviceConnectivity(address);
                } else {
                    startDeviceListActivity();
                }
            } else {
                printReceipt();
                //printReceipt();
            }
        } else {
            Toast.makeText(InvoiceList.this, getString(R.string.error_bluetooth_not_available), Toast.LENGTH_LONG).show();
            finish();
        }
//        startDeviceListActivity();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the session
        } else {
            if (mBluetoothAdapter != null && mService == null)
                mService = new BluetoothService(this, handler);
        }
    }

    private void printReceipt() {
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        try {
            /*val qrcode = PrinterCommand.getBarCommand("Couturier App!", 0, 3, 6)//
            Command.ESC_Align[2] = 0x01
            sendDataByte(Command.ESC_Align)
            sendDataByte(qrcode)*/

            Command.ESC_Align[2] = 0x01;
            sendDataByte(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x11;
            sendDataByte(Command.GS_ExclamationMark);
            sendDataString(lj.getBaseModal().getDefaults().getCompany().getName().concat("\n\n"));

            Command.ESC_Align[2] = 0x00;
            sendDataByte(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x00;
            sendDataByte(Command.GS_ExclamationMark);
            String str;
            if (account != null) {
                str = String.format(getString(R.string.receipt_top),
                        account.getAccName(),
                        simpleDate.format(invoice.getSaleDate()));
            } else {
                str = "Cash Account: " + accId;
            }
            sendDataString(str);

            Command.ESC_Align[2] = 0x01;
            sendDataByte(Command.ESC_Align);
            sendDataString(String.format(getString(R.string.receipt_order_headings), "Product", "Qty", "Price", "Amount"));

            for (int i = 0; i < invoice.getItems().size(); i++) {
                InvoiceItem currentItem = invoice.getItems().get(i);
                long discountedAmount = Math.round(currentItem.getProPrice() * (1 - (currentItem.getPriceDisc() / 100)));

                Command.ESC_Align[2] = 0x00;
                sendDataByte(Command.ESC_Align);
                String productName = currentItem.getProDesc();
                if (productName.length() > 29) {
                    productName = productName.substring(0, 29);
                    productName = productName + "..";
                }
                sendDataString(String.format(getString(R.string.receipt_order_name), productName));
                Command.ESC_Align[2] = 0x01;
                sendDataByte(Command.ESC_Align);
                sendDataString(String.format(getString(R.string.receipt_order_value),
                        "",
                        String.valueOf(currentItem.getProQty().intValue()),
                        String.valueOf(discountedAmount),
                        String.valueOf(discountedAmount
                                * currentItem.getProQty().intValue())
                ));
            }

            Command.ESC_Align[2] = 0x02;
            sendDataByte(Command.ESC_Align);
            if (!etDiscount.getText().toString().equals("0") && Utility.getPrintBalancePreference()) {
                sendDataString(String.format(getString(R.string.balance_receipt_invoice),
                        tBalance.getText(),
                        etDiscount.getText(),
                        netAmmount.getText(),
                        balance.getText(),
                        newBalance.getText()));
            } else if (Utility.getPrintBalancePreference() && etDiscount.getText().toString().equals("0")) {
                sendDataString(String.format(getString(R.string.balance_without_discount_invoice),
                        netAmmount.getText(),
                        balance.getText(),
                        newBalance.getText()));
            } else if (!Utility.getPrintBalancePreference() && !etDiscount.getText().toString().equals("0")) {
                sendDataString(String.format(getString(R.string.discounted_receipt_payments),
                        tBalance.getText(),
                        etDiscount.getText(),
                        netAmmount.getText()));
            } else {
                sendDataString(String.format(getString(R.string.receipt_payments),
                        tBalance.getText()));
            }

            Command.ESC_Align[2] = 0x01;
            sendDataByte(Command.ESC_Align);
            sendDataString(getString(R.string.receipt_bottom));
/*
            Command.GS_ExclamationMark[2] = 0x11;
            sendDataByte(Command.GS_ExclamationMark);
            sendDataString(getString(R.string.text_thank_you));
*/
            sendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(80));
            sendDataByte(Command.GS_V_m_n);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    private void setItemsAdapter() {
        AdapterInvoiceList adapter = new AdapterInvoiceList(c, invoice.getItems(), this);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    View editOptions = view.findViewById(R.id.edit_options);
                    View originalView = view.findViewById(R.id.original_view);
                    if (editOptions.getVisibility() == View.VISIBLE) {
                        removeFromVisible(i);
                        slideLeft(originalView, editOptions);
                    } else {
                        addToVisible(i);
                        slideRight(originalView, editOptions);
                    }
                }
                return false;
            }
        });
    }

    private void sendDataString(String data) {
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        if (data.length() > 0) {
            try {
                mService.write(data.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void removeFromVisible(int index) {
        boolean is_alreadyAdded = false;
        int index_to_remove = -1;
        for (int i = 0; i < visibleCheckBoxes.size(); i++) {
            if (visibleCheckBoxes.get(i) == index) {
                is_alreadyAdded = true;
                index_to_remove = i;
                break;
            }
        }
        if (is_alreadyAdded) {
            visibleCheckBoxes.remove(index_to_remove);
        }
    }

    private void sendDataByte(byte[] data) {
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        mService.write(data);
    }

    private void addToVisible(int index) {
        boolean is_alreadyAdded = false;
        for (int i = 0; i < visibleCheckBoxes.size(); i++) {
            if (visibleCheckBoxes.get(i) == index) {
                is_alreadyAdded = true;
                break;
            }
        }
        if (!is_alreadyAdded) {
            visibleCheckBoxes.add(index);
        }
    }

    private void slideRight(View viewToAnimate, final View viewToShow) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
        viewToAnimate.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                viewToShow.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void slideLeft(View viewToAnimate, final View viewToShow) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.right_to_left);
        viewToAnimate.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                viewToShow.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EDIT_ITEM:
                if (data != null) {
                    if (data.getExtras() != null) {
                        InvoiceItem updatedInvoice = (InvoiceItem) data.getExtras().get("invoiceItem");
                        if (updatedInvoice != null)
                            for (int i = 1; i < invoice.getItems().size(); i++) {
                                if (invoice.getItems().get(i).getProID().equals(updatedInvoice.getProID())) {
                                    invoice.getItems().set(i, updatedInvoice);
                                    break;
                                }
                            }
                        setCalculations();
                        setItemsAdapter();
                    }
                }
                break;
            case ADD_ITEMS:
                if (data != null) {
                    InvoiceItem invoiceItem = new InvoiceItem();
                    if (data.getExtras() != null) {
                        invoiceItem.setPriceDisc(Utility.getDouble(data.getExtras().getString("priceDisc")));
                        invoiceItem.setProDesc(data.getExtras().getString("proDesc"));
                        invoiceItem.setProID(data.getExtras().getString("proID"));
                        boolean isToReturn = data.getExtras().getBoolean("isToReturn");
                        invoiceItem.setProPrice(Utility.getValidDouble(data.getExtras().getString("proPrice")));
                        if (isToReturn) {
                            invoiceItem.setProQty(0 - Utility.getValidDouble(data.getExtras().getString("proQty")));
                        } else {
                            invoiceItem.setProQty(Utility.getValidDouble(data.getExtras().getString("proQty")));
                        }
                        invoiceItem.setSalDetDesc("Added by Application");
                        invoiceItem.setSalDetDesc("");
                        invoiceItem.setSaleID(invoice.getSaleId());

                        ArrayList<InvoiceItem> tempInvoices = invoice.getItems();
                        tempInvoices.add(invoiceItem);

                        invoice.setItems(tempInvoices);
                        setCalculations();
                        setItemsAdapter();
                    }
                }
                break;
            case REQUEST_CONNECT_DEVICE:
                if (data != null) {
                    if (data.getExtras() != null) {
                        String deviceAddress = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                        attemptDeviceConnectivity(deviceAddress);
                    }
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                // Bluetooth is now enabled, so set up a session
                mService = new BluetoothService(this, handler);
                break;

        }
    }

    private void attemptDeviceConnectivity(String address) {
        // Get the BluetoothDevice object
        if (BluetoothAdapter.checkBluetoothAddress(address)) {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            // Attempt to connect to the device
            mService.connect(device);
        }
    }

    @Override
    public void OnDelete(int position) {
        invoice.getItems().remove(position);
        setCalculations();
        setItemsAdapter();
    }

    private void startDeviceListActivity() {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    @Override
    public void OnEdit(int position) {
        InvoiceItem invoiceItem = invoice.getItems().get(position);
        Intent intent = new Intent(InvoiceList.this, AddInvoice.class);
        intent.putExtra("invoiceItem", invoiceItem);
        startActivityForResult(intent, EDIT_ITEM);
    }
}
