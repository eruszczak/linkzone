package pl.reryk.linkzone.utils;

import pl.reryk.linkzone.exception.ImageUploadException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MultipartFileValidator {

    private static final String ERROR_NO_IMAGE = "no_image";
    private static final String ERROR_BAD_SIZE = "size_too_big";
    private static final String ERROR_BAD_IMAGE = "unrecognized_image";
    private static final String ERROR_BAD_DIMENSIONS = "invalid_dimensions";
    private static final String ERROR_READ_IMAGE = "read_error";

    public static void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ImageUploadException(ERROR_NO_IMAGE);
        }
    }

    public static void validateImageSize(MultipartFile file, final int maxSizeInKB) {
        if (file.getSize() > maxSizeInKB * 1000) {
            throw new ImageUploadException(ERROR_BAD_SIZE);
        }
    }

    public static void validateImageDimensions(MultipartFile file, int maxHeightInPx, int maxWidthInPx) {
        BufferedImage bufferedImage = getBufferedImage(file);
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();
        if (height > maxHeightInPx || width > maxWidthInPx) {
            throw new ImageUploadException(ERROR_BAD_DIMENSIONS);
        }
    }

    private static BufferedImage getBufferedImage(MultipartFile file) {
        try {
            InputStream in = new ByteArrayInputStream(file.getBytes());
            BufferedImage buf = ImageIO.read(in);
            if (buf == null) {
                throw new ImageUploadException(ERROR_BAD_IMAGE);
            }
            return buf;
        } catch (IOException e) {
            throw new ImageUploadException(ERROR_READ_IMAGE);
        }
    }
}
