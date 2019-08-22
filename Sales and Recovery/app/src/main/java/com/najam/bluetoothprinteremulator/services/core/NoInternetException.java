package com.najam.bluetoothprinteremulator.services.core;


import com.najam.bluetoothprinteremulator.general.Constants;

import java.io.IOException;

/**
 * Created on 2016-12-03 15:01.
 *
 * @author Rana
 */
public class NoInternetException extends IOException {
    @Override
    public String getMessage() {
        return Constants.ERROR_INTERNET_CONNECTION;
    }

} // NoInternetException
