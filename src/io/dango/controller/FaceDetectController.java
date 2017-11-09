package io.dango.controller;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import io.dango.entity.User;
import io.dango.pojo.DangoError;
import io.dango.repository.UserRepository;
import io.dango.utility.FaceDetectTool;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by MainasuK on 2017-7-3.
 */
@RestController
public class FaceDetectController {

    UserRepository userRepository;

    @Autowired
    public FaceDetectController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public DangoError noPhoto(Exception e) {
        return new DangoError(103, "需要先上传自拍");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public DangoError unknown(Exception e) {
        e.printStackTrace();
        return new DangoError(102,"未知错误");
    }

    @RequestMapping(path = "/face/detect", method = RequestMethod.POST, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] detect(@RequestParam("photo") MultipartFile photo) throws IOException {
        InputStream in = new ByteArrayInputStream(photo.getBytes());
        BufferedImage image = new FaceDetectTool().detechFace(ImageIO.read(in));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(baos);
        encoder.encode(image);
        return baos.toByteArray();
    }

    @RequestMapping(path = "/face/upload/register", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> register(@RequestParam("photo") MultipartFile photo, Principal principal) throws IOException {
        InputStream in = new ByteArrayInputStream(photo.getBytes());
        FaceDetectTool tool = new FaceDetectTool();

        tool.detechFace(ImageIO.read(in));

        final String username = principal.getName();
        String uploadPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/uploads/" + username);

        File folder = new File(uploadPath);
        File file = new File(uploadPath, UUID.randomUUID() + "." + photo.getOriginalFilename().split("\\.")[1]);
        System.out.println(file.getPath());


        if (!folder.exists()) {
            folder.mkdir();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        photo.transferTo(file);
        userRepository.setUserNeedUploadFace(username, false);


        Map<String, Object> map = new HashMap<>();
        map.put("number", tool.faceNumberForDetected);
        map.put("username", username);

        return ResponseEntity.ok(map);
    }

    @RequestMapping(path = "/face/number", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> number(@RequestParam("photo") MultipartFile photo) throws  IOException {
        InputStream in = new ByteArrayInputStream(photo.getBytes());
        FaceDetectTool tool = new FaceDetectTool();

        tool.detechFace(ImageIO.read(in));

        Map<String, Object> map = new HashMap<>();
        map.put("number", tool.faceNumberForDetected);

        return ResponseEntity.ok(map);
    }

    @RequestMapping(path = "/face/pay", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> pay(@RequestParam("photo") MultipartFile photo, @RequestParam Double cost, Principal principal) throws IOException, NullPointerException {
        System.out.println("Paying…");

        String username = principal.getName();
        User user = userRepository.getUserByUsername(username);

        if (user.getNeedface()) {
            throw new NullPointerException();
        }

        InputStream in = new ByteArrayInputStream(photo.getBytes());
        FaceDetectTool tool = new FaceDetectTool();

        tool.compareFace(username, ImageIO.read(in));

        Map<String, Object> map = new HashMap<>();
        map.put("pay", cost);
        map.put("confidence", tool.confidence);

        return ResponseEntity.ok(map);
    }


}
