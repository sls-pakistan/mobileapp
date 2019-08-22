package com.najam.bluetoothprinteremulator.services.core;

import android.content.Context;
import android.support.annotation.NonNull;

import com.najam.bluetoothprinteremulator.general.Constants;
import com.najam.bluetoothprinteremulator.general.Utility;

import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created on 2016-12-03 15:01.
 *
 * @author Rana
 */

public class BaseService implements Callback<String> {

    private Result<String> result;
    private boolean runInBackground;
    protected Context context;
    private int requestId;

    public BaseService(Context context, boolean runInBackground, Result<String> result) {
        this.result = result;
        this.requestId = 0;
        this.context = context;
        this.runInBackground = runInBackground;
    }

    public BaseService(Context context, boolean runInBackground, int requestId, Result<String> result) {
        this.result = result;
        this.requestId = requestId;
        this.context = context;
        this.runInBackground = runInBackground;
    }

    @Override
    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
        hideDailog();
        if (response.isSuccessful()) {
            result.onSuccess(response.body(), requestId);
        } else {
            if (response.errorBody() != null) {
                result.onFailure(response.errorBody().byteStream().toString(), requestId);
            }
        }
    }

    @Override
    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
        hideDailog();
        if (t instanceof NoInternetException) {
            if (!runInBackground) {
                Utility.AlertDialogMessage(context, t.getMessage());
            }
        } else if (t instanceof TimeoutException) {
            if (!runInBackground) {
                Utility.AlertDialogMessage(context, Constants.ERROR_TIMEOUT_REQUEST);
            }
        } else {
            if (!runInBackground) {
                Utility.AlertDialogMessage(context, Constants.ERROR_SOMETHING_WENT_WRONG);
            }
        }
        result.onError(t, requestId);
    }

    protected void showDialog() {
        if (!runInBackground) {
            Utility.showProgressbar(context, Constants.LOADING);
        }
    }

    private void hideDailog() {
        if (!runInBackground) {
            Utility.hideProgressbar();
        }
    } // hideDailog

} // BaseService
