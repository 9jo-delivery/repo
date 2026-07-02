package com.sparta.delivery.global.common;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@CreatedBy
	@Column(updatable = false)
	private Long createdBy;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	@LastModifiedBy
	private Long updatedBy;

	private LocalDateTime deletedAt;

	private Long deletedBy;

	@Column(nullable = false)
	private boolean isDeleted = false;

	// 삭제 처리 메서드
	public void markAsDeleted(Long deletedBy) {
		this.isDeleted = true;
		this.deletedAt = LocalDateTime.now();
		this.deletedBy = deletedBy;
	}
}