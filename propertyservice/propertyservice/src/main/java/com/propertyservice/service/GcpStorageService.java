package com.propertyservice.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GcpStorageService {

    private static final String BUCKET_NAME =
            "propertyservice-images-2026";

    private final Storage storage;

    public GcpStorageService(Storage storage) {
        this.storage = storage;
    }

    public List<String> uploadImages(
            MultipartFile[] files) {

        List<String> imageUrls = new ArrayList<>();

        try {

            for (MultipartFile file : files) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
                storage.create(blobInfo, file.getBytes());

                String imageUrl = String.format("https://storage.googleapis.com/%s/%s", BUCKET_NAME, fileName);

                imageUrls.add(imageUrl);
            }

            return imageUrls;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to upload images",
                    e);
        }
    }
}