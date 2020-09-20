package com.chdlsp.alice.domain.repository;

import com.chdlsp.alice.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    List<UserEntity> findAll();
    Optional<UserEntity> findById(Long id);
    Optional<UserEntity> findByEmail(String email);
}