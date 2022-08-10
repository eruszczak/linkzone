package io.eryk.linkzone.controller;

import io.eryk.linkzone.dto.UploadFileResponse;
import io.eryk.linkzone.security.CurrentUser;
import io.eryk.linkzone.security.UserPrincipal;
import io.eryk.linkzone.service.FileStorageService;
import io.eryk.linkzone.utils.MultipartFileValidator;
import io.eryk.linkzone.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/files")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/upload")
    public UploadFileResponse uploadMedia(@RequestParam("data") MultipartFile file, @CurrentUser UserPrincipal currentUser) {
        Utils.checkIfAuthenticated(currentUser);
        MultipartFileValidator.validate(file);
        MultipartFileValidator.validateImageDimensions(file, 4000, 4000);
        MultipartFileValidator.validateImageSize(file, 2000);
        String filename = fileStorageService.storeFile(file);
        return new UploadFileResponse(filename);
    }
}