package com.najam.bluetoothprinteremulator.services.interceptors;


import android.support.annotation.NonNull;

import com.najam.bluetoothprinteremulator.general.Utility;
import com.najam.bluetoothprinteremulator.services.core.NoInternetException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created on 2016-12-03 15:01.
 *
 * @author Rana
 */


public class ConnectivityInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        return chain.proceed(chain.request());
        /*
        if (!Utility.IS_INTERNET_AVAILABLE(Utility.context)) {
            throw new NoInternetException();
        } else {
            return chain.proceed(chain.request());
        }
        */
    } // intercept

} // ConnectivityInterceptor
