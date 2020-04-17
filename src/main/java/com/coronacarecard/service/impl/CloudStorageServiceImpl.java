package com.coronacarecard.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.service.CloudStorageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class CloudStorageServiceImpl implements CloudStorageService {
    private static final Logger log = LogManager.getLogger(CloudStorageServiceImpl.class);

    @Autowired
    private              AmazonS3 client;

    @Override
    public Bucket createFolder(String bucketName) throws InternalException {
        // TODO (arun) Implement this method
        return null;
    }

    @Override
    public PutObjectResult uploadImage(String bucketName, String imageName, byte[] image, Optional<String> contentType)
            throws InternalException {
        PutObjectResult result = null;

        try (InputStream dataStream = new ByteArrayInputStream(image);) {
            // Set Object Metadata like content-type and file name
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType.isPresent() ? contentType.get() : "image/jpeg");
            metadata.addUserMetadata("x-amz-meta-title", imageName);
            metadata.setContentLength(image.length);


            PutObjectRequest request = new PutObjectRequest(bucketName,
                    imageName,
                    dataStream,
                    metadata);

            // Set object ACL to Public Read
            request.setCannedAcl(CannedAccessControlList.PublicRead);

            // Upload a file as a new object with ContentType and title specified.
            result = client.putObject(request);

        } catch (IOException exp) {
            throw new InternalException(exp.getMessage());
        }

        return result;
    }

    @Override
    public String getObjectUrl(String bucketName, String imageName) throws InternalException {
        return client.getUrl(bucketName, imageName).toString();
    }


}
