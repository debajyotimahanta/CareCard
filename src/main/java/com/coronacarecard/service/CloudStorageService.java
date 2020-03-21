package com.coronacarecard.service;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.coronacarecard.exceptions.InternalException;

import java.util.Optional;

public interface CloudStorageService {
    Bucket createFolder(String bucketName) throws InternalException;
    PutObjectResult uploadImage(String bucketName, String imageName, byte[] image, Optional<String> contentType) throws InternalException;
    String getObjectUrl(String bucketName, String imageName) throws InternalException;
}
