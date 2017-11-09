package io.dango.test;

import io.dango.utility.FaceDetectTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class FaceDetectToolTest {

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: run() 
* 
*/ 
@Test
public void testRun() throws Exception { 
//TODO: Test goes here...
    BufferedImage image = ImageIO.read(getClass().getResource("3.jpg"));
    BufferedImage img = new FaceDetectTool().detechFace(image);

    ImageIO.write(img, "jpg", new File(getClass()
            .getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .getPath()+"3.jpg"));

} 


} 
