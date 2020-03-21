package com.coronacarecard.service.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.service.AWSS3Service;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class AWSS3ServiceImpl implements AWSS3Service {

    private static final String AWS_ACCESS_KEY        = "AKIAQECDVEZ5V4CES3KO";
    private static final String AWS_ACCESS_SECRET_KEY = "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";

    @Override
    public Bucket createBucket(String bucketName) throws InternalException {
        // TODO Implement this method
        return null;
    }

    @Override
    public PutObjectResult uploadImage(String imageName, byte[] image) throws InternalException {
        String          bucketName     = "hjqurnwjjwhb";
        String          fileObjKeyName = "JKh2bdy712h2.jpg";
        PutObjectResult result         = null;
        AmazonS3        client         = getAWSClient();

        try (InputStream dataStream = new ByteArrayInputStream(image);) {
            // Set Object Metadata like content-type and file name
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");
            metadata.addUserMetadata("x-amz-meta-title", imageName);


            PutObjectRequest request = new PutObjectRequest(bucketName,
                    fileObjKeyName,
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

    private AmazonS3 getAWSClient() {

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(AWS_ACCESS_KEY,
                AWS_ACCESS_SECRET_KEY);

        AmazonS3 client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

        return client;
    }
}
