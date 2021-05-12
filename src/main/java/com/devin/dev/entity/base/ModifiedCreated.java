package com.devin.dev.entity.base;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class ModifiedCreated {

    @CreatedDate
    @Column(updatable = false)
    LocalDateTime createdDate;

    @LastModifiedDate
    LocalDateTime lastModifiedDate;

    @CreatedBy
    @Column(updatable = false)
    String createdBy;

    @LastModifiedBy
    String lastModifiedBy;

}