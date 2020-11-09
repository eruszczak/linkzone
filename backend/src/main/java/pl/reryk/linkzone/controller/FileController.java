package pl.reryk.linkzone.controller;

import pl.reryk.linkzone.dto.UploadFileResponse;
import pl.reryk.linkzone.security.CurrentUser;
import pl.reryk.linkzone.security.UserPrincipal;
import pl.reryk.linkzone.service.FileStorageService;
import pl.reryk.linkzone.utils.MultipartFileValidator;
import pl.reryk.linkzone.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping(value = "/api/files")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    private FileStorageService fileStorageService;

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