package com.abdelkader.backend;

import com.abdelkader.backend.modals.CountryModel;
import com.abdelkader.backend.modals.ImageDTO;
import com.abdelkader.backend.modals.UserDTO;
import com.google.gson.Gson;
import okhttp3.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class RequestsHandler {
    OkHttpClient client;

    private static RequestsHandler instance;

    private RequestsHandler(){
        client = new OkHttpClient();
    }

    public void deleteImageFromId(int id) throws IOException {
        HttpUrl url = Objects.requireNonNull(
                HttpUrl.parse("http://localhost:8090/images")).newBuilder()
                .addQueryParameter("id", String.valueOf(id))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            // Check the response status
            if (response.code() == 404) {
                throw new NoSuchElementException("Image id not found.");
            } else {
                throw new UnknownError("Something wrong happened.");
            }
        }
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
            UserHandler.setCurrUser(new Gson().fromJson(response.body().string(), UserDTO.class));
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
            if(!response.isSuccessful()) {
                throw new Exception("BRUH");
            }
            UserHandler.setCurrUser(new Gson().fromJson(response.body().string(), UserDTO.class));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static RequestsHandler getInstance(){
        if(instance == null)
            instance = new RequestsHandler();

        return instance;
    }

    public boolean sendImage(String title, String description, BufferedImage scaledDown, int width, int height) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(scaledDown, "png", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            MediaType mediaType = MediaType.parse("image/png");
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", title+".png", RequestBody.create(mediaType, imageBytes))
                    .addFormDataPart("title", title)
                    .addFormDataPart("description", description)
                    .addFormDataPart("gridWidth", String.valueOf(width))
                    .addFormDataPart("gridHeight", String.valueOf(height))
                    .addFormDataPart("authorId", "1")
                    .build();

            Request request = new Request.Builder()
                    .url("http://localhost:8090/images/upload")
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return response.isSuccessful();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void renameImage(int id, String newTitle, String newDescription) {
        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("id", String.valueOf(id))
                .add("title", newTitle)
                .add("description", newDescription);

        RequestBody requestBody = formBuilder.build();

        Request request = new Request.Builder()
                .url("http://localhost:8090/images/rename")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }
            // Handle the response if needed
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception
        }
    }

    public List<ImageDTO> findImagesFromId(Integer id) throws IOException {
        HttpUrl url = Objects.requireNonNull(
                        HttpUrl.parse("http://localhost:8090/images")
                ).newBuilder()
                .addQueryParameter("userid", String.valueOf(id))
                .build();

        System.out.println(url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            // Check the response status
            if(response.isSuccessful()){
                return List.of(new Gson().fromJson(response.body().string(), ImageDTO[].class));
            }
            else if (response.code() == 404) {
                throw new NoSuchElementException("Author id not found.");
            } else {
                throw new UnknownError("Something wrong happened.");
            }
        }
    }
}
