package com.najam.bluetoothprinteremulator.services.interceptors;


import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created on 2016-12-03 15:01.
 *
 * @author Rana
 */


public class HeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
//        String auth = Utility.getAuthKey(Utility.context);
//
        Request.Builder builder = chain.request().newBuilder();
//
//        builder.addHeader("Key", Constants.SERVICE_KEY);

        return chain.proceed(builder.build());
    } // intercept

} // HeaderInterceptor


