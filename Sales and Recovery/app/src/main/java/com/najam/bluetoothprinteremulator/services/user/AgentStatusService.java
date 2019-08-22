package com.najam.bluetoothprinteremulator.services.user;

import android.content.Context;

import com.najam.bluetoothprinteremulator.general.Utility;
import com.najam.bluetoothprinteremulator.services.clients.AgentClient;
import com.najam.bluetoothprinteremulator.services.core.BaseService;
import com.najam.bluetoothprinteremulator.services.core.Result;
import com.najam.bluetoothprinteremulator.services.core.ServiceUtils;

/**
 * Created on 4/10/2018.
 *
 * @author TRS-LAPTOP-21
 */
public class AgentStatusService extends BaseService {
    private AgentStatusService(Context context, boolean runInBackground, int requestId, Result<String> result) {
        super(context, runInBackground, requestId, result);
    }

    public static AgentStatusService newInstance(Context context, boolean runInBackground, int requestId, Result<String> result) {
        return new AgentStatusService(context, runInBackground, requestId, result);
    }

    public void callService() {
        showDialog();
        ServiceUtils.getRetrofitCredentials(Utility.getBaseUrl(context), null).create(AgentClient.class)
                .agentStatus(Utility.getAgentCode(context)).enqueue(this);
    }
}