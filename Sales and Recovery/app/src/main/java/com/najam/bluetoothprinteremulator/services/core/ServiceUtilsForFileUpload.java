package com.najam.bluetoothprinteremulator.services.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.najam.bluetoothprinteremulator.services.interceptors.ConnectivityInterceptor;
import com.najam.bluetoothprinteremulator.services.interceptors.HeaderInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created on 2016-12-03 15:01.
 *
 * @author Rana
 */

public class ServiceUtilsForFileUpload {
    private static final int TIME_OUT = 1000;
    private static final Retrofit retrofit = getConfiguredRetrofit();

    public static Retrofit getRetrofit() {
        return retrofit;
    } // getRetrofit

    private static Retrofit getConfiguredRetrofit() {
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();

        okBuilder.addInterceptor(new ConnectivityInterceptor());
        okBuilder.addInterceptor(new HeaderInterceptor());

//        if (BuildConfig.LOG) {
//            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//            okBuilder.addInterceptor(interceptor);
//        } // end if
        okBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        okBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        okBuilder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okBuilder.build())
                .build();

    } // getConfiguredRetrofit

    public static String getBaseUrl() {
        String baseUrl = null;
        // TODO: 3/14/2017 setup urls here
//        if (BuildConfig.USE_PRODUCTION_URL) {
//            baseUrl = "";
//        } else {
//            baseUrl = "";
//        }
        return baseUrl;
    }

    public static void log(String tag, String msg) {
//        Utility.log(tag, msg);
    } // log

    //
    public static JsonArray getDataArray(String data) {
        return new JsonParser().parse(data).getAsJsonObject().get("Data").getAsJsonArray();
    }
} // ServiceUtils
