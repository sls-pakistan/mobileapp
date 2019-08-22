package com.najam.bluetoothprinteremulator.services.core;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.najam.bluetoothprinteremulator.services.interceptors.ConnectivityInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created on 2016-12-03 15:01.
 *
 * @author Rana
 */
public class ServiceUtils {
    private static final int TIME_OUT = 30;

    public static Retrofit getRetrofitCredentials(String baseUrl, Interceptor headerInterceptor) {
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();

        okBuilder.addInterceptor(new ConnectivityInterceptor());
        if (headerInterceptor != null)
            okBuilder.addInterceptor(headerInterceptor);
/*Temp
        if (BuildConfig.LOG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okBuilder.addInterceptor(interceptor);
        } // end if
        */
        okBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        okBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        okBuilder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);

       /*CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        okBuilder.cookieJar(new JavaNetCookieJar(cookieManager));*/
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okBuilder.build())
                .build();
    } // getRetrofit
} // ServiceUtils
// */
