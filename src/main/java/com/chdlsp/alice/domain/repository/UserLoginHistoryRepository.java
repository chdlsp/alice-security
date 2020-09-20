package com.chdlsp.alice.domain.repository;

import com.chdlsp.alice.domain.entity.UserLoginHistoryEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserLoginHistoryRepository extends CrudRepository<UserLoginHistoryEntity, Long> {

    Optional<UserLoginHistoryEntity> findById(Long id);
    Optional<UserLoginHistoryEntity> findByEmail(String email);
}