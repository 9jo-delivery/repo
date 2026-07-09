package com.sparta.delivery.domain.ai.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.domain.ai.entity.AiDescriptionLog;

public interface AiDescriptRepository extends JpaRepository<AiDescriptionLog, UUID> , AiReviewCustomRepo{

}
