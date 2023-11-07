package com.goodnews.member.common.domain;

import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseConnectEntity {
    @LastModifiedDate
    private LocalDateTime locationTime;

    @LastModifiedDate
    private LocalDateTime lastUpdate;



}
