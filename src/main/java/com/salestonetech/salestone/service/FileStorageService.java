package com.salestonetech.salestone.service;

import java.io.InputStream;

public interface FileStorageService {
    InputStream getFileContent(String fileKey);
}
