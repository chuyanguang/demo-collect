package com.example.demo.filesys;

import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@SpringBootTest
class DemoFilesysApplicationTests {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Test
    void fileUploadTest01() {
        File file = new File("/Users/ygc/Desktop/a0001.jpg");
        Set<MetaData> metaDataSet = new HashSet<>();
        metaDataSet.add(new MetaData("name", "a0001.jpg"));
        metaDataSet.add(new MetaData("author", "zxx"));
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            StorePath storePath = fastFileStorageClient.uploadFile(fileInputStream, file.length(), "jpg", metaDataSet);
            log.info("{}", storePath.getFullPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void fileDownloadTest01(){
        String fullPath = "group1/M00/00/00/rBD6lF9l5G-AApuKAIi4fpzlLls832.jpg";
        StorePath storePath = StorePath.parseFromUrl(fullPath);
        byte[] bytes = fastFileStorageClient.downloadFile(storePath.getGroup(), storePath.getPath(), new DownloadByteArray());
        try(FileOutputStream fileOutputStream = new FileOutputStream("/Users/ygc/Desktop/test.jpg")) {
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void fileDelTest(){
        String fullPath = "group1/M00/00/00/rBD6kV9loHeAaWrDAIi4fpzlLls855.jpg";
        fastFileStorageClient.deleteFile(fullPath);
    }

}
