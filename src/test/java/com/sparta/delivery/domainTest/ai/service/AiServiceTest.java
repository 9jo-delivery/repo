package com.sparta.delivery.domainTest.ai.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sparta.delivery.domain.ai.repository.AiDescriptRepository;
import com.sparta.delivery.domain.ai.service.AiService;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {
	@InjectMocks
	private AiService aiService;

	@Mock
	private AiService mockAiService;

	@Mock
	private AiDescriptRepository mockAiDescriptRepository;


}
