package com.chdlsp.alice.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ImageUploadEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String imageName; // 이미지 명
    private Long imageSize; // 이미지 사이즈
    private String uploadUser; // 업로드 유저

    @CreatedDate
    private LocalDateTime createdAt; // 업로드 시점
}
