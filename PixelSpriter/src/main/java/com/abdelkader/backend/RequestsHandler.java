package com.abdelkader.backend;

import com.abdelkader.backend.modals.CountryModel;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;

public class RequestsHandler {
    OkHttpClient client;

    private static RequestsHandler instance;

    private RequestsHandler(){
        client = new OkHttpClient();
    }


    public CountryModel[] getAllCountries(){
        Request request = new Request.Builder()
                .url("http://localhost:8090/api/v1/countries")
                .build();
        try (Response response = client.newCall(request).execute()) {
            return new Gson().fromJson(response.body().string(), CountryModel[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean requestLogin(String email, String password){
        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url("http://localhost:8090/api/v1/users/login")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.code() == 200;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void signUp(String email, String username, String password, int country_id) throws Exception {
        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .add("username", username)
                .add("password", password)
                .add("country_id", String.valueOf(country_id))
                .build();

        Request request = new Request.Builder()
                .url("http://localhost:8090/api/v1/users/signup")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if(!response.isSuccessful())
                throw new Exception("BRUH");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static RequestsHandler getInstance(){
        if(instance == null)
            instance = new RequestsHandler();

        return instance;
    }
}
