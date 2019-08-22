package com.najam.bluetoothprinteremulator.services.user;

import android.content.Context;

import com.najam.bluetoothprinteremulator.general.Utility;
import com.najam.bluetoothprinteremulator.services.clients.AgentClient;
import com.najam.bluetoothprinteremulator.services.core.BaseService;
import com.najam.bluetoothprinteremulator.services.core.Result;
import com.najam.bluetoothprinteremulator.services.core.ServiceUtils;

import static com.najam.bluetoothprinteremulator.general.Constants.getInput;
import static com.najam.bluetoothprinteremulator.general.Constants.increment;

/**
 * Created on 4/10/2018.
 *
 * @author TRS-LAPTOP-21
 */
public class SynDataService extends BaseService {
    private SynDataService(Context context, boolean runInBackground, int requestId, Result<String> result) {
        super(context, runInBackground, requestId, result);
    }

    public static SynDataService newInstance(Context context, boolean runInBackground, int requestId, Result<String> result) {
        return new SynDataService(context, runInBackground, requestId, result);
    }

    public void callService() {
        showDialog();
        ServiceUtils.getRetrofitCredentials(Utility.getBaseUrl(context), null).create(AgentClient.class)
                .syncData(Utility.getAgentCode(context), getInput).enqueue(this);
    }

    public void callService(String inputFeed) {
        showDialog();
        ServiceUtils.getRetrofitCredentials(Utility.getBaseUrl(context), null).create(AgentClient.class)
                .syncData(Utility.getAgentCode(context), increment, inputFeed).enqueue(this);
    }
}
