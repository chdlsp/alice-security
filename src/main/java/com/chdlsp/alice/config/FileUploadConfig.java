package com.chdlsp.alice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("storage")
public class FileUploadConfig {
    /**
     * Folder location for storing files
     */
    private String location = "C:\\UploadFiles\\";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
