package io.dango.controller;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.TextUtils;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginControllerTest {
    String URL = "http://localhost:8080";

    @Test
    public void login() throws Exception {
        String username = "user";
        String password = "USER";

        OAuthClientRequest request = null;
        try {
            request = OAuthClientRequest
                    .authorizationLocation("http://localhost:8080/oauth/authorize")
                    .setClientId("android")
                    .setResponseType("code")
                    .buildQueryMessage();
        }
        catch (OAuthSystemException e) {
            System.out.println(e.getLocalizedMessage());
        }

        System.out.println(request.getLocationUri());
    }

    @Test
    public void loginToken() throws IOException {
        String url = URL + "/oauth/token";
        Content content = Request.Post(url)
                .addHeader("Accept","application/json")
                .addHeader("Authorization","Basic YW5kcm9pZDpxaWRpYW4=")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                .bodyForm(
                        Form.form()
                                .add("password", "USER")
                                .add("response_type", "code")
                                .add("scope", "read write")
                                .add("grant_type", "password")
                                .add("username", "user")
                                .add("client_secret", "qidian")
                                .add("client_id", "android")
                                .build())
                .execute().returnContent();

        System.out.println(content.toString());
        JSONObject jsonObject = new JSONObject(content.toString());
        String token = jsonObject.getString("access_token");
    }

    @Test
    public void addJson() throws IOException{
        String url = URL + "/register";
        Content content = Request.Post(url)
                .addHeader("Accept","application/json")
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .bodyString("{\"username\": \"2UserName\",\"password\": \"myPassword\"}", ContentType.APPLICATION_JSON)
                .execute().returnContent();
        System.out.println(content.toString());
    }

    @Test
    public void urlConnection() {
        String url = "http://localhost:8080/register";
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", "user233");
        map.put("password", "pass");
        JSONObject jsonObject = new JSONObject(map);
        try {
            java.net.URL object = new URL(url);
            HttpURLConnection con = (HttpURLConnection) object.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);

            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json, charset=utf-8");
            con.getOutputStream().write(String.valueOf(jsonObject).getBytes());

            int code = con.getResponseCode();
            System.out.println(code);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }
    }

    @Test
    public void okHttp() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", "wangma32");
        map.put("password", "233333");
        JSONObject jsonObject = new JSONObject(map);
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        client = builder.build();
        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("http://localhost:8080/register")
//                .url("http://localhost:8080/register")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json, charset=utf-8")
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            System.out.println(response);
            if(response.isSuccessful()) {
                String result = response.body().string();
                if(!TextUtils.isEmpty(result)) {
                    JSONObject obj = new JSONObject(result);
                    System.out.println(obj);
                    String token = obj.getJSONObject("auth").getString("access_token");
                    boolean success = obj.getBoolean("success");
                    System.out.println("token:" + token + ", success:" + success);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void loginFeedBack() throws IOException {
        String url = URL + "/login";
        Content content = Request.Post(url)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .bodyString("{\"username\": \"user\",\"password\": \"USER\"}", ContentType.APPLICATION_JSON)
                .execute().returnContent();

        System.out.println(content.toString());
        JSONObject jsonObject = new JSONObject(content.toString());
        String token = jsonObject.getJSONObject("auth").getString("access_token");
        boolean needface = jsonObject.getJSONObject("user").getBoolean("needface");

        System.out.println(token +" "+ needface);

    }
}