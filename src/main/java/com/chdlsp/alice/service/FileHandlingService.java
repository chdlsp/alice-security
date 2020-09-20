package com.chdlsp.alice.service;

import com.chdlsp.alice.domain.entity.ImageUploadEntity;
import com.chdlsp.alice.domain.repository.ImageUploadRepository;
import com.chdlsp.alice.interfaces.exception.ImageNotExistedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FileHandlingService {

    @Autowired
    private ImageUploadRepository imageUploadRepository;

    public ImageUploadEntity getFileInfo(String fileName) {

        Optional<ImageUploadEntity> imageEntityByImageName = imageUploadRepository.findByImageName(fileName);

        if(imageEntityByImageName.isPresent()) {
            return imageEntityByImageName.get();
        } else {
            throw new ImageNotExistedException(fileName);
        }
    }
}
