package com.chdlsp.alice.domain.repository;

import com.chdlsp.alice.domain.entity.ImageUploadEntity;
import com.chdlsp.alice.domain.entity.UserLoginHistoryEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ImageUploadRepository extends CrudRepository<ImageUploadEntity, Long> {

    Optional<ImageUploadEntity> findById(Long id);

}
