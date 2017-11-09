package io.dango.controller;

import io.dango.entity.User;
import io.dango.pojo.DangoError;
import io.dango.repository.JDBCUserRepository;
import io.dango.utility.AuthTool;
import io.dango.utility.JSONTool;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    JDBCUserRepository jdbcUserRepository;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public DangoError unknown(Exception e) {
        return new DangoError(102,"未知错误");
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> login(@RequestBody String json) throws IOException {
        JSONObject jsonObject = new JSONObject(json);
        final String username = jsonObject.getString("username");
        final String password = jsonObject.getString("password");

        User user = jdbcUserRepository.getUserByUsername(username);
        Map<String, Object> auth = AuthTool.auth(username, password);

        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("auth", auth);


        return ResponseEntity.ok(map);
    }

    @RequestMapping(path = "/userinfo", method = RequestMethod.GET)
    public ResponseEntity<User> userinfo(Principal principal) {

        String username = principal.getName();
        return ResponseEntity.ok(jdbcUserRepository.getUserByUsername(username));
    }
}
