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

import static org.junit.jupiter.api.Assertions.*;

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
        String imageName = "myimage.jpg";
        byte[] values = {1, 2, 3, 4, 5};
        AWSS3Service target = new AWSS3ServiceImpl();
        PutObjectResult result = null;

        // Act
        try {
            result = target.uploadImage(imageName, values);
        }catch(InternalException exp) {
            assertFalse(true);
        }

        // Assert
        assertNotNull(result);
    }

//    @Test
//    void getObjectUrl() {
//        // Arrange
//        String imageName = "myimage.jpg";
//        AWSS3Service target = new AWSS3ServiceImpl();
//        String result = null;
//
//        // Act
//        try {
//            result = target.getObjectUrl(imageName);
//        }catch(InternalException exp) {
//            assertFalse(true);
//        }
//
//        // Assert
//        assertNotNull(result);
//    }
}