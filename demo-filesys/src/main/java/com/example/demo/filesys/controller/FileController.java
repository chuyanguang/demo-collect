package com.example.demo.filesys.controller;

import com.example.demo.filesys.utils.MinIOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@RestController
public class FileController {

    @Autowired
    private MinIOUtil minioUtil;

    @PostMapping("/upload")
    public String MinIOUpload(MultipartFile file) {
        if (file.isEmpty() || file.getSize() == 0) {
            return "文件为空";
        }
        try {
            if (!minioUtil.bucketExists("javakf")) {
                minioUtil.makeBucket("javakf");
            }

            String fileName = file.getOriginalFilename();
            String newName = "image/" + UUID.randomUUID().toString().replaceAll("-", "")
                    + fileName.substring(fileName.lastIndexOf("."));

            InputStream inputStream = file.getInputStream();
            minioUtil.putObject("javakf", newName, inputStream);
            inputStream.close();

            String url = minioUtil.getObjectUrl("javakf", newName);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return "上传失败";
        }
    }

}
