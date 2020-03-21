package com.coronacarecard.service.impl;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.service.AWSS3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase
@SpringBootTest
class AWSS3ServiceTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void createBucket() {
    }

    @Test
    void uploadImage() {
        // Arrange
        File imageFile = new File("/home/amukherjee/Pictures/Test.jpg");
        byte[] values = null;
        String imageName = "myimage.jpg";
        String bucketName = "hjqurnwjjwhb";
        AWSS3Service target = new AWSS3ServiceImpl();
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
        }

        // Assert
        assertNotNull(result);
    }

    @Test
    void getObjectUrl() {
        // Arrange
        String imageName = "myimage.jpg";
        String bucketName = "hjqurnwjjwhb";
        AWSS3Service target = new AWSS3ServiceImpl();
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