package com.najam.bluetoothprinteremulator.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.najam.bluetoothprinteremulator.AdapterInvoiceList;
import com.najam.bluetoothprinteremulator.DataModalComponent;
import com.najam.bluetoothprinteremulator.R;
import com.najam.bluetoothprinteremulator.general.Constants;
import com.najam.bluetoothprinteremulator.general.Utility;
import com.najam.bluetoothprinteremulator.modal.Account;
import com.najam.bluetoothprinteremulator.modal.Invoice;
import com.najam.bluetoothprinteremulator.modal.InvoiceItem;
import com.najam.bluetoothprinteremulator.print.helper.BluetoothService;
import com.najam.bluetoothprinteremulator.print.helper.DeviceListActivity;
import com.najam.bluetoothprinteremulator.print.sdk.Command;
import com.najam.bluetoothprinteremulator.print.sdk.PrinterCommand;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.najam.bluetoothprinteremulator.InvoiceList.REQUEST_CONNECT_DEVICE;

public class PrintInvoiceActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String INVOICE_ID = "invoice_id";
    public static final String ACCOUNT_ID = "account_id";

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

    private TextView nameTV, totalAmountTV, discountTV, netAmountTV, invoiceIdTV, receivedAmountTV;
    private View discountWrapper, netAmountWrapper, receivedAmountWrapper;
    private ListView itemsLV;
    private Button printInvoice, btnClose;
    private String invoiceId, accountId;
    private BluetoothAdapter btAdapter;
    private BluetoothService btService;
    private String mConnectedDeviceName;
    private boolean isPrinterConnected;
    private double totalBalance = 0.0;

    private DataModalComponent component;
    private Invoice invoice;
    private Account account;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //TODO: Handle different types of messages
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Toast.makeText(PrintInvoiceActivity.this, getString(R.string.title_connected_to) + " " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                            isPrinterConnected = true;
                            printReceipt();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Toast.makeText(PrintInvoiceActivity.this, getString(R.string.title_connecting), Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            Toast.makeText(PrintInvoiceActivity.this, getString(R.string.title_not_connected), Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    break;
                case MESSAGE_READ:
                    break;
                case MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Utility.saveBluetoothDevice(mConnectedDeviceName, PrintInvoiceActivity.this);
                    Toast.makeText(PrintInvoiceActivity.this, "Connected to $mConnectedDeviceName", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(PrintInvoiceActivity.this, msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_CONNECTION_LOST:
                    Toast.makeText(PrintInvoiceActivity.this, "Device connection was lost", Toast.LENGTH_SHORT).show();
                    isPrinterConnected = false;
                    break;
                case MESSAGE_UNABLE_CONNECT:
                    Toast.makeText(PrintInvoiceActivity.this, "Unable to connect device", Toast.LENGTH_SHORT).show();
                    Utility.saveBluetoothDevice("", PrintInvoiceActivity.this);
                    startDeviceListActivity();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_invoice);

        invoiceId = getIntent().getStringExtra(INVOICE_ID);
        accountId = getIntent().getStringExtra(ACCOUNT_ID);

        intitViews();
        setClickListeners();
        initPrinterResources();
        setData();
    }

    private void setData() {
        try {
            component = DataModalComponent.getInstance(this);
            invoice = component.getInvoiceById(invoiceId);
            account = component.getAccountByAccountID(accountId);

            if (invoice.getItems().get(0).getSaleID() == null) {
                invoice.getItems().remove(0);
            }
            for (int i = 0; i < invoice.getItems().size(); i++) {
                Double priceDisc = invoice.getItems().get(i).getPriceDisc();
                Double price = invoice.getItems().get(i).getProPrice();
                Double qty = invoice.getItems().get(i).getProQty();
                Double amt = qty * price * (1 - priceDisc / 100);
                totalBalance += amt;
            }

            invoiceIdTV.setText(invoice.getSaleId());
            netAmountTV.setText(String.valueOf(totalBalance - invoice.getDiscRate()));
            discountTV.setText(String.valueOf(invoice.getDiscRate()));
            totalAmountTV.setText(String.valueOf(Math.round(totalBalance)));
            nameTV.setText(accountId);
            receivedAmountTV.setText(String.valueOf(invoice.getCashPaid()));
            if (invoice.getItems().get(0).getSaleID() == null) {
                invoice.getItems().remove(0);
            }
            invoice.getItems().add(0, new InvoiceItem());
            if (invoice.getDiscRate() != 0) {
                discountWrapper.setVisibility(View.VISIBLE);
                netAmountWrapper.setVisibility(View.VISIBLE);
            }

            if (invoice.getCashPaid() != 0) {
                receivedAmountWrapper.setVisibility(View.VISIBLE);
            }
            AdapterInvoiceList adapter = new AdapterInvoiceList(this, invoice.getItems());
            itemsLV.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void intitViews() {
        nameTV = (TextView) findViewById(R.id.name_tv);
        totalAmountTV = (TextView) findViewById(R.id.total_amount_tv);
        discountTV = (TextView) findViewById(R.id.discount_tv);
        netAmountTV = (TextView) findViewById(R.id.net_amount_tv);
        invoiceIdTV = (TextView) findViewById(R.id.invoice_id);
        itemsLV = (ListView) findViewById(R.id.items_listview);
        printInvoice = (Button) findViewById(R.id.print_invoice);
        btnClose = (Button) findViewById(R.id.btn_close);
        receivedAmountTV = (TextView) findViewById(R.id.received_amountTV);
        discountWrapper = findViewById(R.id.discount_wrapper);
        if(Build.VERSION.SDK_INT<= Build.VERSION_CODES.JELLY_BEAN){
            discountWrapper.setBackgroundColor(Color.WHITE) ;
        }
        netAmountWrapper = findViewById(R.id.net_amount_wrapper);
        receivedAmountWrapper = findViewById(R.id.received_amount_view);
    }

    private void setClickListeners() {
        printInvoice.setOnClickListener(this);
        btnClose.setOnClickListener(this);
    }

    private void initPrinterResources() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (btAdapter != null && btService == null)
                btService = new BluetoothService(this, handler);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.print_invoice:
                if (btAdapter != null) {
                    if (!isPrinterConnected) {
                        String address = Utility.getBluetoothDevice();
                        if (!address.equals("")) {
                            attemptDeviceConnectivity(address);
                        } else {
                            startDeviceListActivity();
                        }
                    } else {
                        printReceipt();
                    }
                } else {
                    Toast.makeText(PrintInvoiceActivity.this, getString(R.string.error_bluetooth_not_available), Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case R.id.btn_close:
                finish();
                break;
        }
    }

    private void startDeviceListActivity() {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    private void attemptDeviceConnectivity(String address) {
        // Get the BluetoothDevice object
        if (BluetoothAdapter.checkBluetoothAddress(address)) {
            BluetoothDevice device = btAdapter.getRemoteDevice(address);
            // Attempt to connect to the device
            btService.connect(device);
        }
    }

    private void printReceipt() {
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        try {
            Command.ESC_Align[2] = 0x01;
            sendDataByte(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x11;
            sendDataByte(Command.GS_ExclamationMark);
            sendDataString(component.getBaseModal().getDefaults().getCompany().getName().concat("\n\n"));

            Command.ESC_Align[2] = 0x00;
            sendDataByte(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x00;
            sendDataByte(Command.GS_ExclamationMark);
            String str = String.format(getString(R.string.receipt_top),
                    account.getAccName(),
                    simpleDate.format(invoice.getSaleDate()));
            sendDataString(str);

            Command.ESC_Align[2] = 0x01;
            sendDataByte(Command.ESC_Align);
            sendDataString(String.format(getString(R.string.receipt_order_headings), "Product", "Qty", "Price", "Amount"));

            for (int i = 1; i < invoice.getItems().size(); i++) {
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
            if (invoice.getDiscRate() == 0) {
                sendDataString(String.format(getString(R.string.discounted_receipt_payments),
                        String.valueOf(totalBalance),
                        String.valueOf(invoice.getDiscRate()),
                        String.valueOf(totalBalance - invoice.getDiscRate())));
            } else {
                sendDataString(String.format(getString(R.string.receipt_payments),
                        String.valueOf(totalBalance)));
            }

            Command.ESC_Align[2] = 0x01;
            sendDataByte(Command.ESC_Align);
            sendDataString(getString(R.string.receipt_bottom));
            Command.GS_ExclamationMark[2] = 0x11;
            sendDataByte(Command.GS_ExclamationMark);
//            sendDataString(getString(R.string.text_thank_you));

            sendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(80));
            sendDataByte(Command.GS_V_m_n);
        } catch (Exception e) {
            finish();
            Toast.makeText(this, Constants.ERROR_SOMETHING_WENT_WRONG, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        finish();
    }


    private void sendDataString(String data) {
        if (btService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        if (data.length() > 0) {
            try {
                btService.write(data.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendDataByte(byte[] data) {
        if (btService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        btService.write(data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
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
                btService = new BluetoothService(this, handler);
                break;
        }
    }
}
