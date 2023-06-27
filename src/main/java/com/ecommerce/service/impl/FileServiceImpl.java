package com.ecommerce.service.impl;

import com.ecommerce.service.IFileService;
import com.ecommerce.util.FTPUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Implementation of the IFileService interface for file upload.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * Uploads a file to the specified path.
     *
     * @param file the file to be uploaded
     * @param path the path to upload the file to
     * @return the filename of the uploaded file, or null if the upload fails
     */
    public String upload(MultipartFile file, String path) {
        // Get the original filename of the uploaded image
        String originalFilename = file.getOriginalFilename();
        // Get the file extension
        String extensionFile = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        // Generate a unique filename with the extension
        String fileName = UUID.randomUUID() + "." + extensionFile;
        logger.info("Start uploading file. Original filename: {}, Upload path: {}, New filename: {}", originalFilename, path, fileName);
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            // Set file directory to be writable
            fileDir.setWritable(true);
            // Create multiple level directories if they don't exist
            fileDir.mkdirs();
        }
        // Target file
        File targetFile = new File(path, fileName);

        try {
            // Transfer the file to the target file on the server
            file.transferTo(targetFile);
            // Upload the file from the server to the FTP file server
            FTPUtil.uploadFiles(Lists.newArrayList(targetFile));
            // Delete the file on the server to prevent excessive server load
            targetFile.delete();
        } catch (IOException e) {
            logger.error("File upload failed", e);
            return null;
        }
        return targetFile.getName();
    }
}
