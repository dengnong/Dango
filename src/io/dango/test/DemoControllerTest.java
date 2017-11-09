package io.dango.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by 54472 on 2017/6/30.
 */
public class DemoControllerTest {

    HttpClient httpClient;

    @Before
    public void before() {
        httpClient = new HttpClient();
    }

    @After
    public void after() {
        httpClient = null;
    }

    // Use HttpClient Get test
    @Test
    public void demo() throws Exception {
        HttpMethod method = new GetMethod("http://localhost:8080/demo");
        int statusCode = httpClient.executeMethod(method);

        // Assert HTTP request return OK
        Assert.assertEquals(HttpStatus.OK.value(), statusCode);

        byte[] responseBody = method.getResponseBody();

        // Assert HTTP response body not null
        Assert.assertNotNull(responseBody);

        ObjectMapper mapper = new ObjectMapper();
        String body = new String(responseBody);
        List<String> strList = mapper.readValue(body, new TypeReference<List<String>>() { });

        // Assert JSON parse
        Assert.assertEquals("Hello", strList.get(0));
        Assert.assertEquals("World", strList.get(1));
    }

    @Test
    public void error() throws Exception {
        HttpMethod method = new GetMethod("http://localhost:8080/error");
        int statusCode = httpClient.executeMethod(method);

        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), statusCode);

        System.out.println(HttpStatus.NOT_FOUND.value()+" "+statusCode);
    }

    // Use RestTemplate test via getForObject form 1
    @Test
    public void faceNotFound() throws Exception {
        int id = 777;
        RestTemplate restTemplate = new RestTemplate();

        HttpClientErrorException error = null;
        try {
            restTemplate.getForObject("http://localhost:8080/findFace/{uid}", Error.class, id);
        } catch (HttpClientErrorException e) {
            error = e;
        }

        Assert.assertNotNull(error);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, ?> body = mapper.readValue(error.getResponseBodyAsString(), new TypeReference<Map<String, ?>>() { });

        Assert.assertEquals(2, body.get("code"));
        Assert.assertEquals("face not match of by user id " + id, body.get("message"));
    }

    @Test
    public void faceByID() throws Exception {

    }

    // Use HttpEntity and RestTemplate.postForObject to POST JSON to server
    @Test
    public void saveFace() throws Exception {
        // Set HTTP header Content-Type to "application/json"
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<String> entity = new HttpEntity<>("Face-Content", headers);
        RestTemplate template = new RestTemplate();
        String response =  template.postForObject("http://localhost:8080/saveFace", entity, String.class);

        // Assert response not null
        Assert.assertNotNull(response);
    }

    @Test
    public void saveFace2() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<String> entity = new HttpEntity<>("Face2-Content", headers);
        RestTemplate template = new RestTemplate();

//        use RestTemplate.postForObject

//        String response = template.postForObject("http://localhost:8080/saveFace2", entity, String.class);
//        Assert.assertNotNull(response);

//        use RestTemplate.exchange
        
        ResponseEntity<ArrayList<String>> responseEntity = template.exchange(
                "http://localhost:8080/saveFace2",
                org.springframework.http.HttpMethod.POST,
                entity,
                (Class<ArrayList<String>>) new ArrayList<String>().getClass());

        Assert.assertNotNull(responseEntity);
        URI url = responseEntity.getHeaders().getLocation();
        Assert.assertEquals("http://localhost:8080/findFace/233", url.toString());


    }

    // Use RestTemplate.exchange to POST JSON to server & locate URI
    @Test
    public void saveFace3() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<String> entity = new HttpEntity<>("Face3-Content", headers);
        RestTemplate template = new RestTemplate();
        ResponseEntity<ArrayList<String>> responseEntity = template.exchange(
                "http://localhost:8080/saveFace3",
                org.springframework.http.HttpMethod.POST,
                entity,
                (Class<ArrayList<String>>) new ArrayList<String>().getClass());

        Assert.assertNotNull(responseEntity);
        Assert.assertNotNull(responseEntity.getBody());
        Assert.assertNotNull(responseEntity.getHeaders());

        URI url = responseEntity.getHeaders().getLocation();
        Assert.assertEquals("http://localhost:8080/face/233", url.toString());
    }

    @Test
    public void testJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject("{\"username\":\"user\"}");
        Assert.assertEquals("user", jsonObject.get("username"));
    }

}