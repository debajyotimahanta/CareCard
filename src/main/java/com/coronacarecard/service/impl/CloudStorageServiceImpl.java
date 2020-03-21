package com.coronacarecard.service.impl;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.service.CloudStorageService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class CloudStorageServiceImpl implements CloudStorageService {

    private static final String AWS_ACCESS_KEY        = "";
    private static final String AWS_ACCESS_SECRET_KEY = "";

    @Override
    public Bucket createFolder(String bucketName) throws InternalException {
        // TODO (arun) Implement this method
        return null;
    }

    @Override
    public PutObjectResult uploadImage(String bucketName, String imageName, byte[] image, Optional<String> contentType)
            throws InternalException {
        PutObjectResult result = null;
        AmazonS3Client  client = getAWSClient();

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
        AmazonS3Client client = getAWSClient();
        return client.getResourceUrl(bucketName, imageName);
    }

    private AmazonS3Client getAWSClient() {

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(AWS_ACCESS_KEY,
                AWS_ACCESS_SECRET_KEY);

        // Using AmazonS3Client over AmazonS3 interface because AmazonS3Client exposes
        // method to get object url.
        AmazonS3Client client = (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .build();

        return client;
    }
}
