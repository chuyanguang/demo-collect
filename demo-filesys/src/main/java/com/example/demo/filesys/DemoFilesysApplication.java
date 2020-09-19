package com.example.demo.filesys;

import com.github.tobato.fastdfs.FdfsClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(FdfsClientConfig.class)
@SpringBootApplication
public class DemoFilesysApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoFilesysApplication.class, args);
    }

}
