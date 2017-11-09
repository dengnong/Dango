package io.dango.controller;

import io.dango.entity.User;
import io.dango.pojo.DangoError;
import io.dango.repository.JDBCUserRepository;
import io.dango.utility.AuthTool;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 54472 on 2017/7/7.
 */

@RestController
public class RegisterController {

    @Autowired
    JDBCUserRepository jdbcUserRepository;

    @ExceptionHandler(JSONException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public DangoError forbidden(JSONException e) {
        return new DangoError(100,"用户请求格式错误");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public DangoError duiplicate(DuplicateKeyException e) {
        return new DangoError(101,"用户名被占用");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public DangoError duiplicate(Exception e) {
        return new DangoError(102,"未知错误");
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public Map<String, Object> register(@RequestBody String json) throws JSONException, IOException, IOException {
        JSONObject jsonObject = new JSONObject(json);
        final String username = jsonObject.getString("username");
        final String password = jsonObject.getString("password");

        User user = new User();

        user.setUsername(username);
        user.setPassword(password);

        jdbcUserRepository.saveUser(user);

        System.out.println(user);

        User savedUser = jdbcUserRepository.getUserByUsername(username);
        Map<String, Object> auth = AuthTool.auth(username, password);

        Map<String, Object> map = new HashMap<>();
        map.put("user", savedUser);
        map.put("auth", auth);
        map.put("success", true);

        return map;
    }

}
