package io.dango.utility;

import io.dango.entity.User;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class AuthTool {

    static public Map<String, Object>auth(String username, String password) throws IOException {
        final String URL = "http://localhost:8080";
        String url = URL + "/oauth/token";

        String authString = "android" + ":" + "qidian";
        String authStringEnc = new String(Base64.encodeBase64(authString.getBytes("UTF-8")));

        Content content = Request.Post(url)
                .addHeader("Accept","application/json")
                .addHeader("Authorization","Basic " + authStringEnc)
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                .bodyForm(
                        Form.form()
                                .add("username", username)
                                .add("password", password)
                                .add("response_type", "code")
                                .add("scope", "read write")
                                .add("grant_type", "password")
                                .add("client_secret", "qidian")
                                .add("client_id", "android")
                                .build())
                .execute().returnContent();


        JSONObject object = new JSONObject(content.toString());
        Map<String, Object> auth = JSONTool.toMap(object);

        return auth;
    }
}
