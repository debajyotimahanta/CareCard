package com.coronacarecard.service.impl;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.service.CloudStorageService;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
//TODO (arun) a unit test needs to run in everyone desktop without any special access
@Ignore
public class CloudStorageServiceTest {


    @Test
    public void createBucket() {
    }


    //@Test
    //public void uploadImage() {
        // Arrange
        //TODO (arun) please create this file as a part of setup and then remove Ignore
       /* File imageFile = new File("src/test/resources/Test.jpg");
        byte[] values = null;
        String imageName = "myimage.jpg";
        String bucketName = "hjqurnwjjwhb";
        CloudStorageService target = new CloudStorageServiceImpl();
        PutObjectResult result = null;

        // Act
        try {
            values = new FileInputStream(imageFile).readAllBytes();
            result = target.uploadImage(bucketName, imageName, values, Optional.empty());
        }catch(InternalException exp) {
            assertFalse(true);
        } catch (FileNotFoundException e) {
            assertFalse(true);
        } catch (IOException e) {
            assertFalse(true);
        }*/

        // Assert
        //assertNotNull(result);
   // }

    @Test
    public void getObjectUrl() {
        // Arrange
        String imageName = "myimage.jpg";
        String bucketName = "hjqurnwjjwhb";
        CloudStorageService target = new CloudStorageServiceImpl();
        String result = null;

        // Act
        try {
            result = target.getObjectUrl(bucketName, imageName);
        }catch(InternalException exp) {
            assertFalse(true);
        }

        // Assert
        assertNotNull(result);
    }
}