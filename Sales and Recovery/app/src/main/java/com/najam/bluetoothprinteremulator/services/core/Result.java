package com.najam.bluetoothprinteremulator.services.core;

/**
 * Represents result of a service.
 * <p>
 * Created on 2016-12-03 15:01.
 *
 * @author Rana
 * @see BaseService
 */
public interface Result<T> {
    void onSuccess(T data, int requestId);

    void onFailure(String message, int requestId);

    void onError(Throwable throwable, int requestId);
} // Result
