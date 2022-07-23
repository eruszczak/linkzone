package io.eryk.linkzone.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import io.eryk.linkzone.exception.FileStorageException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    private final AmazonClient amazonClient;

    @Autowired
    public FileStorageService(AmazonClient amazonClient) {
        this.amazonClient = amazonClient;
    }

    public String storeFile(MultipartFile multipartFile) {
        String fileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(multipartFile.getOriginalFilename());

        try {
            File file = convertMultiPartToFile(multipartFile);
            amazonClient.getS3client()
                    .putObject(new PutObjectRequest(amazonClient.getBucketName(), fileName, file)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
            return amazonClient.getEndpointUrl() + "/" + fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public void removeFile(String fileName) {
        if (fileName == null) {
            return;
        }
        amazonClient.getS3client()
                .deleteObject(new DeleteObjectRequest(amazonClient.getBucketName(), fileName));
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}