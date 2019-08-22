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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.najam.bluetoothprinteremulator.general.Utility;
import com.najam.bluetoothprinteremulator.modal.Account;
import com.najam.bluetoothprinteremulator.modal.Transaction;
import com.najam.bluetoothprinteremulator.print.helper.BluetoothService;
import com.najam.bluetoothprinteremulator.print.helper.DeviceListActivity;
import com.najam.bluetoothprinteremulator.print.sdk.Command;
import com.najam.bluetoothprinteremulator.print.sdk.PrinterCommand;

import org.json.JSONException;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by nAjam uL hAssAn on 05-02-2017.
 */

public class ReceivePaymentActivity extends Activity {
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_CONNECTION_LOST = 6;
    public static final int MESSAGE_UNABLE_CONNECT = 7;
    public static final int REQUEST_CONNECT_DEVICE = 1214;
    private static final int REQUEST_ENABLE_BT = 1215;

    public static String DEVICE_NAME = "device_name";
    public static String TOAST = "toast";
    Account account;
    String accid, zone, subzone;
    Context c;
    TextView name, balance, address, phn, rbalance;
    EditText rammount;
    Button save;
    // fields for print
    private CheckBox cbPrint;
    private BluetoothAdapter mBTAdapter;
    private boolean isPrinterConnected;
    private BluetoothService mService;
    private DataModalComponent component;
    private Transaction transaction;
    private String mConnectedDeviceAddres;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //TODO: Handle different types of messages
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Toast.makeText(ReceivePaymentActivity.this, getString(R.string.title_connected_to) + " " + mConnectedDeviceAddres, Toast.LENGTH_SHORT).show();
                            isPrinterConnected = true;
                            printReceipt();
                            //printReceipt();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Toast.makeText(ReceivePaymentActivity.this, getString(R.string.title_connecting), Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            Toast.makeText(ReceivePaymentActivity.this, getString(R.string.title_not_connected), Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    break;
                case MESSAGE_READ:
                    break;
                case MESSAGE_DEVICE_NAME:
                    mConnectedDeviceAddres = msg.getData().getString(DEVICE_NAME);
                    Utility.saveBluetoothDevice(mConnectedDeviceAddres, ReceivePaymentActivity.this);
                    Toast.makeText(ReceivePaymentActivity.this, "Connected to $mConnectedDeviceAddres", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(ReceivePaymentActivity.this, msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_CONNECTION_LOST:
                    Toast.makeText(ReceivePaymentActivity.this, "Device connection was lost", Toast.LENGTH_SHORT).show();
                    isPrinterConnected = false;
                    break;
                case MESSAGE_UNABLE_CONNECT:
                    Toast.makeText(ReceivePaymentActivity.this, "Unable to connect device", Toast.LENGTH_SHORT).show();
                    Utility.saveBluetoothDevice("", ReceivePaymentActivity.this);
                    startDeviceListActivity();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receive_payment);

        c = getApplicationContext();
        save = findViewById(R.id.rpsave);
        cbPrint = findViewById(R.id.cbPrint);

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        accid = getIntent().getStringExtra("IAccount");
        zone = getIntent().getStringExtra("Zone");
        subzone = getIntent().getStringExtra("SubZone");

        try{
            this.account = DataModalComponent.getInstance(this).getAccountByAccId(zone, subzone, accid);
        }
        catch (Exception e){
            //TODO: pass
        }


        name = findViewById(R.id.rpname);
        balance = findViewById(R.id.rpbalance);
        address = findViewById(R.id.rpaddress);
        phn = findViewById(R.id.rpphn);
        rbalance = findViewById(R.id.rpnewbal);
        rammount = findViewById(R.id.rpeditbal);

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN){
            rammount.setTextColor(Color.WHITE);
        }
        name.setText(this.account.getAccName());
        balance.setText(NumberFormat.getInstance(Locale.US).format(Math.ceil(this.account.getBalance())));
        address.setText(this.account.getAddress());
        phn.setText(this.account.getPhone());

        rammount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                double amount = Utility.getValidDouble(charSequence.toString());
                rbalance.setText(NumberFormat.getInstance(Locale.US).format(Math.ceil(account.getBalance() - amount)));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    save.setEnabled(false);
                    component = DataModalComponent.getInstance(ReceivePaymentActivity.this);
                    transaction = new Transaction();
                    transaction.setAccId(accid);
                    transaction.setDate(new Date());
                    transaction.setCredit(Utility.getValidDouble(rammount.getText().toString()));
                    transaction.setDebit(0d);
                    transaction.setVouTypeID("RC");
                    transaction.setVouID("rc_" + component.getMaxTrId());
                    transaction.setVouDesc(new Date() + " payment");
                    component.updateTransactions(accid, zone, subzone, transaction);
                    component.updateAccountBalance(accid, zone, subzone, rbalance.getText().toString().replace(",", "."));
                    account.setBalance(account.getBalance() - transaction.getCredit());
                    component.getBaseModal().getNewReceipts().add(transaction);
                    component.saveModalToFile();
                    if (cbPrint.isChecked()) {
                        printInvoice();
                    } else
                        finish();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void printInvoice() {
        if (mBTAdapter != null) {
            if (!isPrinterConnected) {
                String address = Utility.getBluetoothDevice();
                if (!address.equals("")) {
                    attemptDeviceConnectivity(address);
                } else {
                    startDeviceListActivity();
                }
            } else {
                printReceipt();
                printReceipt();
            }
        } else {
            Toast.makeText(ReceivePaymentActivity.this, getString(R.string.error_bluetooth_not_available), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mBTAdapter != null && !mBTAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the session
        } else {
            if (mBTAdapter != null && mService == null)
                mService = new BluetoothService(this, handler);
        }
    }

    private void attemptDeviceConnectivity(String address) {
        // Get the BluetoothDevice object
        if (BluetoothAdapter.checkBluetoothAddress(address)) {
            BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
            // Attempt to connect to the device
            mService.connect(device);
        }
    }

    private void startDeviceListActivity() {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
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
                    simpleDate.format(transaction.getDate()));
            sendDataString(str);

            Command.ESC_Align[2] = 0x01;
            sendDataByte(Command.ESC_Align);

            Command.ESC_Align[2] = 0x02;
            sendDataByte(Command.ESC_Align);

            if (Utility.getPrintBalancePreference()) {
                sendDataString(String.format(getString(R.string.balance_receipt_payments),
                        rammount.getText(),
                        balance.getText(),
                        rbalance.getText()));
            } else {
                sendDataString(String.format(getString(R.string.receipt_payments),
                        rammount.getText()));
            }

            Command.ESC_Align[2] = 0x01;
            sendDataByte(Command.ESC_Align);
            sendDataString(getString(R.string.receipt_bottom));

            sendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(80));
            sendDataByte(Command.GS_V_m_n);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    private void sendDataByte(byte[] data) {
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        mService.write(data);
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
                mService = new BluetoothService(this, handler);
                break;
        }
    }
}
