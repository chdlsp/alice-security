package com.chdlsp.alice.domain.entity;

import com.sun.istack.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserLoginHistoryEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    @NotNull
    private String email;

    @CreatedDate
    private LocalDateTime createdAt; // 로그인

    // TODO 로그인 연장 관련 기능 추후 구현
    // private LocalDateTime refreshAt;

    @LastModifiedDate
    @Setter
    private LocalDateTime loggedOutAt; // 로그아웃

    // userLoginHistory N : 1 user
    @ManyToOne
    @Setter
    private UserEntity userEntity;


}
