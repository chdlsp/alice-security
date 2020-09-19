package com.chdlsp.alice.domain.repository;

import com.chdlsp.alice.domain.entity.User;
import com.chdlsp.alice.domain.entity.UserLoginHistory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserLoginHistoryRepository extends CrudRepository<UserLoginHistory, Long> {

    Optional<UserLoginHistory> findById(Long id);
    Optional<UserLoginHistory> findByEmail(String email);
}