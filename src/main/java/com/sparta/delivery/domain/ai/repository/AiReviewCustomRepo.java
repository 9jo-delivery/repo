package com.sparta.delivery.domain.ai.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.delivery.domain.ai.dto.AiLogDto.AiSearchCondition;
import com.sparta.delivery.domain.ai.entity.AiDescriptionLog;

public interface AiReviewCustomRepo {
	Page<AiDescriptionLog> searchLogsByCondition(AiSearchCondition condition, Pageable pageable);
}
