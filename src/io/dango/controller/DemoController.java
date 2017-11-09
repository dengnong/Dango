package io.dango.controller;

import io.dango.pojo.DangoError;
import io.dango.pojo.FaceNotFoundException;
import io.dango.repository.JDBCUserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MainasuK on 2017-6-30.
 */
@RestController
public class DemoController {

    @Autowired
    JDBCUserRepository jdbcUserRepository;

    // Return hello JSON
    @RequestMapping(path = "/demo", method = RequestMethod.GET)
    public List<String>demo() {
        List<String> list = new ArrayList<>();
        list.add("Hello");
        list.add("World");

        return list;
    }

    // Return 404 with error info
    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public ResponseEntity<?> error() {
        List<String> list = null;
        HttpStatus status = HttpStatus.NOT_FOUND;

        if (null == list) {
            DangoError error = new DangoError(1, "not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(list, status);
    }

    // Return 404 when find face with userID via Exception
    @ExceptionHandler(FaceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public DangoError faceNotFound(FaceNotFoundException e) {
        long userID = e.getUserID();
        return new DangoError(2, "face not match of by user id " + userID);
    }

    // As above
    @RequestMapping(path = "/findFace/{id}", method = RequestMethod.GET)
    public String faceByID(@PathVariable long id) {
        String face = null;
        if (null == face) {
            throw new FaceNotFoundException(id);
        }

        return face;
    }

    // Save face via HTTP POST method
    @RequestMapping(path = "/saveFace", method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public List<String> saveFace(@RequestBody String string) {
        List<String> list = new ArrayList<>();
        list.add(string);
        list.add(string);

        return list;
    }

    // Save face via HTTP POST method and get its URI
    @RequestMapping(path = "/saveFace2", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<List<String>> saveFace2(@RequestBody String string) {
        List<String> list = new ArrayList<>();
        list.add(string);
        list.add(string);
        long faceID = 233;

        URI locationURI = URI.create("http://localhost:8080/findFace/" + faceID);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(locationURI);

        return new ResponseEntity<>(list, headers, HttpStatus.CREATED);
    }

    // Save face via HTTP POST method and get its URI via UriComponentsBuilder
    @RequestMapping(path = "/saveFace3", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<List<String>> saveFace3(@RequestBody String string, UriComponentsBuilder builder) {
        List<String> list = new ArrayList<>();
        list.add(string);
        list.add(string);
        long faceID = 233;

        URI locationURI = builder
                .path("/face/")
                .path(String.valueOf(faceID))
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(locationURI);

        return new ResponseEntity<>(list, headers, HttpStatus.CREATED);
    }

}
