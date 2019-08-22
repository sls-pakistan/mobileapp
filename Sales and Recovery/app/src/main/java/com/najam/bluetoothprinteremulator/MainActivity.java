package com.najam.bluetoothprinteremulator;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.najam.bluetoothprinteremulator.general.Utility;
import com.najam.bluetoothprinteremulator.modal.Feed;
import com.najam.bluetoothprinteremulator.modal.StatusServiceModel;
import com.najam.bluetoothprinteremulator.modal.SyncServiceModel;
import com.najam.bluetoothprinteremulator.services.core.Result;
import com.najam.bluetoothprinteremulator.services.user.AgentStatusService;
import com.najam.bluetoothprinteremulator.services.user.SynDataService;
import com.najam.bluetoothprinteremulator.ui.ConfigurationActivity;
import com.najam.bluetoothprinteremulator.ui.SummaryActivity;
import com.najam.bluetoothprinteremulator.ui.ViewPaymentsActivity;

import org.json.JSONException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import io.fabric.sdk.android.Fabric;

import static com.najam.bluetoothprinteremulator.InvoiceList.ACCOUNT_ID_KEY;
import static com.najam.bluetoothprinteremulator.InvoiceList.ACCOUNT_NAME_KEY;

public class MainActivity extends Activity
        implements View.OnClickListener, Result<String>, Utility.DialogCallBacks {
    private static final int REQUEST_PICK_FILE = 1;
    private final int STATUS_REQUEST_ID = 1100;
    private final int SYNC_REQUEST_ID = 1101;

    String restoredText;
    DataModalComponent lj;
    Context c;
    TextView companyName;
    private Button zoneBtn, summaryBtn, syncDataBtn, addCashInvoice, viewPayments, resetBtn;
    private ImageView settingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Utility.context = getApplicationContext();
        initViews();

        SharedPreferences prefs = getSharedPreferences("AddressPref", MODE_PRIVATE);
        restoredText = prefs.getString("Address", null);

        companyName = findViewById(R.id.matvName);
        setCompanyName();

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(300);
                        if (restoredText != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    summaryBtn.setEnabled(true);
                                    zoneBtn.setEnabled(true);
                                    addCashInvoice.setEnabled(true);
                                    viewPayments.setEnabled(true);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    summaryBtn.setEnabled(false);
                                    zoneBtn.setEnabled(false);
                                    addCashInvoice.setEnabled(false);
                                    viewPayments.setEnabled(false);
                                }
                            });
                            Intent intent = new Intent(MainActivity.this, ConfigurationActivity.class);
                            startActivity(intent);
                        }
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                // TODO
            }
        };

        thread.start();
    }

    private void setCompanyName() {
        if (restoredText != null) {
            try {
                lj = DataModalComponent.getInstance(this);
                if (lj.getBaseModal() != null)
                    companyName.setText(lj.getBaseModal().getDefaults().getCompany().getName());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initViews() {
        zoneBtn = findViewById(R.id.zones);
        summaryBtn = findViewById(R.id.summary);
        syncDataBtn = findViewById(R.id.sync_data);
        settingsBtn = findViewById(R.id.settings_btn);
        addCashInvoice = findViewById(R.id.add_cash_invoice);
        viewPayments = findViewById(R.id.view_payments);
        resetBtn = findViewById(R.id.reset);
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        zoneBtn.setOnClickListener(this);
        summaryBtn.setOnClickListener(this);
        syncDataBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);
        addCashInvoice.setOnClickListener(this);
        viewPayments.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
    }

    public void openZonesActivity() {
        Intent ZIntent = new Intent(MainActivity.this, ZoneActivity.class);
        startActivity(ZIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK_FILE:
                    if (data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {
                        File selectedFile = new File(data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
                        SharedPreferences.Editor editor = getSharedPreferences("AddressPref", MODE_PRIVATE).edit();
                        editor.putString("Address", selectedFile.getPath());
                        editor.apply();
                        Intent Zintent = new Intent(MainActivity.this, ZoneActivity.class);
                        startActivity(Zintent);
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.zones:
                openZonesActivity();
                break;
            case R.id.summary:
                startActivity(new Intent(this, SummaryActivity.class));
                break;
            case R.id.sync_data:
                AgentStatusService.newInstance(this, false, STATUS_REQUEST_ID, this).callService();
                break;
            case R.id.settings_btn:
                startActivity(new Intent(this, ConfigurationActivity.class));
                break;
            case R.id.add_cash_invoice:
                Intent intent = new Intent(this, InvoiceList.class);
                intent.putExtra(ACCOUNT_ID_KEY, lj.getBaseModal().getDefaults().getCashAccId());
                intent.putExtra(ACCOUNT_NAME_KEY, "Cash Invoice");
                this.lj.setInvoiceInProgress(null);
                startActivity(intent);
                break;
            case R.id.view_payments:
                startActivity(new Intent(this, ViewPaymentsActivity.class));
                break;
            case R.id.reset:
                LoadJson lg = new LoadJson();
                lg.resetAsset(getApplicationContext());
                lj.setBaseModal(null);
                summaryBtn.setEnabled(false);
                zoneBtn.setEnabled(false);
                addCashInvoice.setEnabled(false);
                viewPayments.setEnabled(false);
                resetBtn.setEnabled(false);

                break;
        }
    }

    @Override
    public void onSuccess(String data, int requestId) {
        switch (requestId) {
            case SYNC_REQUEST_ID:
                SyncServiceModel syncServiceModel = new Gson().fromJson(data, SyncServiceModel.class);
                switch (syncServiceModel.getStatus()) {
                    case 0:
                        Utility.AlertDialogMessage(this, syncServiceModel.getMessage());
                        break;
                    case 1:
                        if (syncServiceModel.getFeed() != null) {
                            Feed feed = syncServiceModel.getFeed();
                            String dataToSave = feed.getInputFeed();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    exportJsonString(dataToSave);
                                } else {
                                    getPermissions(dataToSave);
                                }
                            }else{
                                exportJsonString(dataToSave);
                            }
                        } else {
                            Utility.AlertDialogMessage(this, syncServiceModel.getMessage());
                        }
                        break;
                }
                break;
            case STATUS_REQUEST_ID:
                StatusServiceModel model = new Gson().fromJson(data, StatusServiceModel.class);
                switch (model.getStatus()) {
                    case 0:
                        Utility.AlertDialogMessage(this, "No Open Feed");
                        break;
                    case 1:
                        DataModalComponent component;
                        try {
                            component = DataModalComponent.getInstance(this);
                            if (component.getBaseModal() == null) {
                                SynDataService.newInstance(this, false, SYNC_REQUEST_ID, this)
                                        .callService();
                            } else {
                                SynDataService.newInstance(this, false, SYNC_REQUEST_ID, this)
                                        .callService(component.getDataToUpload(this));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        DataModalComponent component1;
                        try {
                            component1 = DataModalComponent.getInstance(this);
                            if (component1.getBaseModal() == null) {
                                SynDataService.newInstance(this, false, SYNC_REQUEST_ID, this)
                                        .callService();
                            } else {
                                Utility.AlertDialogMessageWithCallbacks(this, "You have already updated feed. Do you want to upload data!", this);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;

                }
                break;
        }
    }

    @Override
    public void onFailure(String message, int requestId) {
    }

    @Override
    public void onError(Throwable throwable, int requestId) {
    }

    private void getPermissions(final String data) {
        //Get permissions on runtime if OS version is higher than 6.0
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // User has granted permissions
                        exportJsonString(data);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                        // User denied permissions

                    }
                }).check();
    }

    private void exportJsonString(String data) {
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        //Name of file in local Storage
        String fileName = Utility.getJSONFileName();
        // Name of separate director under the application name
        String AppDirector = Utility.getAppDirectoy();
        String dirPath = baseDir + File.separator + AppDirector;

        File tempfile = new File(dirPath);
        if (!tempfile.exists() || !tempfile.isDirectory()) {
            if (tempfile.mkdir()) {
                System.out.println("Directory created");
            } else {
                System.out.println("Directory is not created");
            }
        }

        String filePath = baseDir + File.separator + AppDirector + File.separator + fileName;
        File f = new File(filePath);

        // File exist
        if (!f.exists() || f.isDirectory()) {
            f = new File(filePath);
        }
        try {
            FileWriter fileWriter = new FileWriter(f.getAbsoluteFile());
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(data);
            bufferedWriter.close();
            SharedPreferences.Editor editor = getSharedPreferences("AddressPref", MODE_PRIVATE).edit();
            editor.putString("Address", filePath);
            editor.apply();

            restoredText = filePath;
            summaryBtn.setEnabled(true);
            zoneBtn.setEnabled(true);
            addCashInvoice.setEnabled(true);
            viewPayments.setEnabled(true);
            resetBtn.setEnabled(true);

            setCompanyName();
        } catch (IOException e) {
            Utility.showToast("File could not be exported");
            e.printStackTrace();
        }
    }

    @Override
    public void OnOkPressed() {
        DataModalComponent component;
        try {
            component = DataModalComponent.getInstance(this);
            SynDataService.newInstance(this, false, SYNC_REQUEST_ID, this)
                    .callService(component.getDataToUpload(this));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}