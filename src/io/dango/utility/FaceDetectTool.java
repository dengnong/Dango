package io.dango.utility;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_face;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.MatOfRect;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoader;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.IntBuffer;

import static org.bytedeco.javacpp.opencv_face.createEigenFaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createFisherFaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.opencv.core.CvType.*;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;

/**
 * Created by 54472 on 2017/7/3.
 */
public class FaceDetectTool {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public Integer faceNumberForDetected = 0;
    public Double confidence = 0.0;

    public BufferedImage detechFace(BufferedImage bufferedImage) throws IOException {

        BufferedImage image = bufferedImage;
        int rows = image.getHeight();
        int cols = image.getWidth();
        int type = 0;
        switch (image.getType()) {
            case BufferedImage.TYPE_3BYTE_BGR:
                type = CV_8UC3;
                break;

            case BufferedImage.TYPE_BYTE_GRAY:
                type = CV_8UC1;
                break;

            case BufferedImage.TYPE_4BYTE_ABGR:
                type = CV_8UC4;
                break;
        }

        //chang image to Mat from BufferedImage
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        org.opencv.core.Mat image_final = new org.opencv.core.Mat(rows, cols, type);
        image_final.put(0,0, pixels);

        System.out.println(getClass().getResource("lbpcascade_frontalface.xml").getPath());
        ClassPathResource resource = new ClassPathResource("lbpcascade_frontalface.xml", getClass());
        System.out.println(resource.getFile().getCanonicalPath());

        CascadeClassifier faceDetector = new CascadeClassifier(resource.getFile().getCanonicalPath());

        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image_final, faceDetections);

        faceNumberForDetected = faceDetections.toArray().length;
        System.out.println(String.format("Detected %s faces", faceNumberForDetected));

        for(org.opencv.core.Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(image_final, new org.opencv.core.Point(rect.x, rect.y), new org.opencv.core.Point(rect.x + rect.width, rect.y + rect.height), new org.opencv.core.Scalar(0, 255, 0), 5);

        }

//        String filename = "faceDetection.png";
//        System.out.println(String.format("Writing %s", filename));
//        Imgcodecs.imwrite(filename, image_final);
        return matToBufferedImage(image_final);

    }

    private BufferedImage matToBufferedImage(org.opencv.core.Mat matrix) {
        int cols = matrix.cols();
        int rows = matrix.rows();
        int elemSize = (int)matrix.elemSize();
        byte[] data = new byte[cols * rows * elemSize];
        int type;

        matrix.get(0, 0, data);

        switch (matrix.channels()) {
            case 1:
                type = BufferedImage.TYPE_BYTE_GRAY;
                break;

            case 3:
                type = BufferedImage.TYPE_3BYTE_BGR;

                // bgr to rgb
                byte b;
                for(int i=0; i<data.length; i=i+3) {
                    b = data[i];
                    data[i] = data[i+2];
                    data[i+2] = b;
                }
                break;

            default:
                return null;
        }

        BufferedImage image = new BufferedImage(cols, rows, type);
        image.getRaster().setDataElements(0, 0, cols, rows, data);

        return image;
    }

    public BufferedImage compareFace(String username, BufferedImage newImage) throws IOException {

        String facePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/uploads/" + username);
        File folder = new File(facePath);

        FilenameFilter imgFilter = (dir, name) -> {
            name = name.toLowerCase();
            return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png") || name.endsWith(".bmp");
        };
        File[] imageFiles = folder.listFiles(imgFilter);

        opencv_core.MatVector images = new opencv_core.MatVector(imageFiles.length);

        opencv_core.Mat labels = new opencv_core.Mat(1, imageFiles.length, CV_32SC1);
        IntBuffer labelsBuf = labels.createBuffer();

        int counter = 0;

        for (File image : imageFiles) {
            opencv_core.Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

            images.put(counter, img);
            labelsBuf.put(counter, 1);

            counter++;
        }

//        opencv_face.FaceRecognizer faceRecognizer = createFisherFaceRecognizer();
//         opencv_face.FaceRecognizer faceRecognizer = createEigenFaceRecognizer();
         opencv_face.FaceRecognizer faceRecognizer = createLBPHFaceRecognizer();

        faceRecognizer.train(images, labels);

        IntPointer label = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(1);
        opencv_core.Mat image = bufferedImageToMat(newImage);
        cvtColor(image, image, COLOR_RGB2GRAY);
        faceRecognizer.predict(image, label, confidence);


        int predictedLabel = label.get(0);
        this.confidence = confidence.get();

        System.out.println("Predicted label: " + predictedLabel + ", with confidence: " + confidence);

        return newImage;
    }

    public opencv_core.Mat bufferedImageToMat(BufferedImage bi) {
        opencv_core.Mat mat = new opencv_core.Mat(bi.getHeight(), bi.getWidth(), CV_8UC(3));

        int r, g, b;
        UByteRawIndexer indexer = mat.createIndexer();
        for (int y = 0; y < bi.getHeight(); y++) {
            for (int x = 0; x < bi.getWidth(); x++) {
                int rgb = bi.getRGB(x, y);

                r = (byte) ((rgb >> 0) & 0xFF);
                g = (byte) ((rgb >> 8) & 0xFF);
                b = (byte) ((rgb >> 16) & 0xFF);

                indexer.put(y, x, 0, r);
                indexer.put(y, x, 1, g);
                indexer.put(y, x, 2, b);
            }
        }
        indexer.release();
        return mat;
    }

}
