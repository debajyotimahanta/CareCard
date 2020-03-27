package com.coronacarecard.service.impl;

import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.service.CryptoService;
import com.coronacarecard.util.TestHelper;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"AWS_ARN=arn:aws:kms:us-west-1:008731829883:key/a72c4b37-325e-4254-9a9f-38592d01e0b2",
        "spring.app.forntEndBaseUrl=http://base"})
class CryptoServiceImplTest {

    @Autowired
    CryptoService target;

    @Before
    public void setup() {

    }

    @Test
    public void encoding() {
        // Arrange
        String sampleData = TestHelper.getPlainTextPlaceId();

        // Act
        String result = target.encrypt(sampleData);

        // Assert
        assertNotNull(result);
    }

    @Test
    void decode() {
        // Arrange
        String cipherData = TestHelper.getEncryptedPlaceId();

        // Act
        String result = null;
        try {
            result = target.decrypt(cipherData);
        } catch (InternalException e) {
            e.printStackTrace();
        }

        // Assert
        assertNotNull(result);
        System.out.println(result);
    }
}