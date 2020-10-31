package io.careerfirst.facedetectdemo;


import org.springframework.web.multipart.MultipartFile;

public class FileUploadModel {
    private MultipartFile image;

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
