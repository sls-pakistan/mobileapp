package com.najam.bluetoothprinteremulator.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.najam.bluetoothprinteremulator.R;
import com.najam.bluetoothprinteremulator.general.Utility;

public class ConfigurationActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText merchantCode, urlToServices;
    private CheckBox printBalanceCheckbox;
    private Button saveInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        initViews();
    }

    private void initViews() {
        merchantCode = (EditText) findViewById(R.id.merchant_code);
        urlToServices = (EditText) findViewById(R.id.url_to_services);
        saveInfo = (Button) findViewById(R.id.save_info);
        printBalanceCheckbox = (CheckBox) findViewById(R.id.print_balance_checkbox);

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN){
            merchantCode.setTextColor(Color.WHITE);
            urlToServices.setTextColor(Color.WHITE);
        }

        String agentCode = Utility.getAgentCode(this);
        String servicesUrl = Utility.getBaseUrl(this);

        printBalanceCheckbox.setChecked(Utility.getPrintBalancePreference());

        if (!agentCode.equals("")) {
            merchantCode.setText(agentCode);
            merchantCode.setSelection(agentCode.length());
        }
        if (!servicesUrl.equals("")) {
            urlToServices.setText(servicesUrl);
            merchantCode.setSelection(agentCode.length());
        }
        setClickListeners();
    }

    private void setClickListeners() {
        saveInfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_info:
                validateAndSave();
                finish();
                break;
        }
    }

    private void validateAndSave() {
        String url = urlToServices.getText().toString();
        String agentCode = merchantCode.getText().toString();
        if (TextUtils.isEmpty(url)
            //|| !Patterns.WEB_URL.matcher(url).matches()
        )
        {
            urlToServices.setError("Please enter a valid url");
            return;
        }
        if (TextUtils.isEmpty(agentCode)) {
            merchantCode.setError("Please enter a valid agent code");
            return;
        }
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        Utility.saveAgentInfoToPrefs(url, agentCode, this);
        if (printBalanceCheckbox.isChecked()) {
            Utility.savePrintBalancePreference(true);
        } else {
            Utility.savePrintBalancePreference(false);
        }
    }
}
