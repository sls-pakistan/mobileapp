package com.najam.bluetoothprinteremulator.services.clients;

import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created on 2016-12-03 15:01.
 *
 * @author Rana
 */
public interface AgentClient {

    @Multipart
    @POST("sync.php")
    Call<String> syncData(@Part("agent") String agentCoed, @Part("request_type") String requestType);

    @Multipart
    @POST("sync.php")
    Call<String> syncData(@Part("agent") String agentCoed, @Part("request_type") String requestType, @Part("increment_feed") String incrementFeed);

    @Multipart
    @POST("status.php")
    Call<String> agentStatus(@Part("agent") String agentCoed);

} // AgentClient
