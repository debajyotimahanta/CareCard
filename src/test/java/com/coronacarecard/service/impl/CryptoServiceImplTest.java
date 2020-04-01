package com.coronacarecard.service.impl;

import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.service.CryptoService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
@RunWith(SpringRunner.class)
@SpringBootTest
//TODO (arun) a unit test needs to run in everyone desktop without any special access
@Ignore
public class CryptoServiceImplTest {

    @Autowired
    CryptoService target;

    @Before
    public void setup() {

    }

    @Test
    public void encoding() {
        // Arrange
        String sampleData = UUID.randomUUID().toString();
        // Act
        byte[] result = target.encrypt(sampleData);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void decode() {
        // Arrange
        String cipherData = UUID.randomUUID().toString();

        // Act
        String result = null;
        try {
            result = target.decrypt(cipherData.getBytes());
        } catch (InternalException e) {
            e.printStackTrace();
        }

        // Assert
        assertNotNull(result);
        System.out.println(result);
    }
}