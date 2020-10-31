package io.careerfirst.facedetectdemo;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

@Controller
public class HomeController {
    static {
        nu.pattern.OpenCV.loadShared();
    }

    private ResourceLoader resourceLoader;

    public HomeController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("fd",new FileUploadModel());
        return "home";
    }

    @PostMapping("/")
    public String home(Model model, @ModelAttribute("fd") FileUploadModel fileUploadModel){
        model.addAttribute("fd",new FileUploadModel());
        try {
            File tempFile = File.createTempFile("image",".png");
            fileUploadModel.getImage().transferTo(tempFile);
            System.out.println(tempFile.getAbsolutePath());
            File fileOnServerWithFaceDetected = File.createTempFile("detected",".png");
            detectFace(tempFile,fileOnServerWithFaceDetected);
            byte[] fileInBytes = Files.readAllBytes(fileOnServerWithFaceDetected.toPath());
            String result = "data:image/png;base64,"+ Base64.getEncoder().encodeToString(fileInBytes);
            model.addAttribute("result",result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(fileUploadModel.getImage().getName());
        return "home";
    }

    private void detectFace(File fileOnServer, File fileOnServerWithFaceDetected){
        try {
            String frontalFace = resourceLoader.getResource("classpath:lbpcascade_frontalface.xml").getFile().getAbsolutePath();
            CascadeClassifier faceDetector = new CascadeClassifier(frontalFace);
            Mat image = Imgcodecs.imread(fileOnServer.getAbsolutePath());
            MatOfRect faceDetections = new MatOfRect();
            faceDetector.detectMultiScale(image,faceDetections);
            System.out.println("Face detected "+faceDetections.toArray().length);
            // draw a bounding box around each face
            for(Rect rect : faceDetections.toArray()){
                Imgproc.rectangle(image,
                        new Point(rect.x,rect.y),
                        new Point(rect.x+rect.width,rect.y+rect.height),
                        new Scalar(255,255,255));
            }
            Imgcodecs.imwrite(fileOnServerWithFaceDetected.getAbsolutePath(),image);
            System.out.println(fileOnServerWithFaceDetected.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
