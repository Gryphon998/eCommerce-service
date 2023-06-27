package com.ecommerce.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FTPUtil {

    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static final String FTP_IP = PropertiesUtil.getProperty("ftp.server.ip");
    private static final String FTP_user = PropertiesUtil.getProperty("ftp.user");
    private static final String FTP_password = PropertiesUtil.getProperty("ftp.pass");
    private static final String REMOTE_PATH = "img";

    private String ip;
    private int port;
    private String user;
    private String password;
    private FTPClient ftpClient;

    /**
     * Constructs an instance of FTPUtil with the specified FTP server details.
     *
     * @param ip       The IP address of the FTP server.
     * @param port     The port number of the FTP server.
     * @param user     The username for FTP authentication.
     * @param password The password for FTP authentication.
     */
    private FTPUtil(String ip, int port, String user, String password) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    /**
     * Uploads a list of files to the FTP server.
     *
     * @param fileList The list of files to be uploaded.
     * @return true if the upload is successful, false otherwise.
     * @throws IOException If an I/O error occurs during the upload process.
     */
    public static boolean uploadFiles(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(FTP_IP, 21, FTP_user, FTP_password);
        logger.info("Starting file upload");
        boolean result = ftpUtil.uploadFiles(REMOTE_PATH, fileList);
        logger.info("File upload finished. Result: {}", result);
        return result;
    }

    private boolean uploadFiles(String remotePath, List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream inputStream = null;
        if (connectServer(this.ip, this.port, this.user, this.password)) {
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File fileItem : fileList) {
                    inputStream = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(), inputStream);
                }
            } catch (IOException e) {
                logger.error("Failed to upload files", e);
                uploaded = false;
                e.printStackTrace();
            } finally {
                inputStream.close();
                ftpClient.disconnect();
            }
        } else {
            uploaded = false;
        }
        return uploaded;
    }

    /**
     * Connects to the FTP server and logs in.
     *
     * @param ip       The IP address of the FTP server.
     * @param port     The port number of the FTP server.
     * @param user     The username for FTP authentication.
     * @param password The password for FTP authentication.
     * @return true if the connection and login are successful, false otherwise.
     */
    private boolean connectServer(String ip, int port, String user, String password) {
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user, password);
        } catch (IOException e) {
            logger.error("Failed to connect to or log in to the FTP server", e);
        }
        return isSuccess;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
