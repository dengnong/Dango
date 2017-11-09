package io.dango.test;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by MainasuK on 2017-7-3.
 */
public class FaceDetectControllerTest {

    @Test
    public void detect() throws Exception {
        RestTemplate template = new RestTemplate();
        template.getMessageConverters().add(new ByteArrayHttpMessageConverter());

        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("photo", new FileSystemResource(getClass().getResource("profile.jpeg").getFile()));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, httpHeaders);
        ResponseEntity<byte[]> responseEntity = template.exchange("http://localhost:8080/face/detect", HttpMethod.POST, requestEntity, byte[].class);

        File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "profile.jpg");
        System.out.println(file.getPath());
        FileOutputStream output = new FileOutputStream(file);
        IOUtils.write(responseEntity.getBody(), output);
    }

}