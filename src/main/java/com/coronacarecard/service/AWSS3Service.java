package com.coronacarecard.service;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.coronacarecard.exceptions.InternalException;

public interface AWSS3Service {
    Bucket createBucket(String bucketName) throws InternalException;
    PutObjectResult uploadImage(String imageName, byte[] image) throws InternalException;
//    String getObjectUrl(String imageName) throws InternalException;
}
