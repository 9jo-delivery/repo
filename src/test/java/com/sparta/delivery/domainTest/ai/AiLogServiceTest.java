package com.sparta.delivery.domainTest.ai;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.sparta.delivery.domain.ai.dto.AiLogDto.AiLogSummaryResponseDto;
import com.sparta.delivery.domain.ai.dto.AiLogDto.AiSearchCondition;
import com.sparta.delivery.domain.ai.entity.AiDescriptionLog;
import com.sparta.delivery.domain.ai.repository.AiDescriptRepository;
import com.sparta.delivery.domain.ai.service.AiDescriptionService;
import com.sparta.delivery.domain.menu.entity.Menu;
import com.sparta.delivery.domain.menu.repository.MenuRepository;
import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.domain.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AiLogServiceTest {
	@InjectMocks
	private AiDescriptionService aiDescriptionService;
	@Mock
	private AiDescriptRepository aiDescriptRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private MenuRepository menuRepository;
	@Mock
	private RestaurantRepository restaurantRepository;

	@Test
	@DisplayName("조건이 붙지 않은 로그 목록 조회 성공")
	void default_LogListTest() {
		Long ownerId = 1L;
		UUID restaurantId = UUID.randomUUID();
		User owner = createUser(ownerId);
		Restaurant restaurant = createRestaurant(restaurantId);

		AiSearchCondition condition = new AiSearchCondition();
		List<AiDescriptionLog> logs = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			UUID menuId = UUID.randomUUID();
			Menu menu = createMenu(menuId);

			AiDescriptionLog log = AiDescriptionLog.create(
				owner, restaurant, menu, "prompt", "txt", "response"
			);
			logs.add(log);
		}
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<AiDescriptionLog> page = new PageImpl<>(logs, pageable, logs.size());

		given(aiDescriptRepository.searchLogsByCondition(any(AiSearchCondition.class), eq(pageable))).willReturn(page);

		// when
		Page<AiLogSummaryResponseDto> reports = aiDescriptionService.searchLogs(condition, pageable);

		// then
		//  서비스 계층에서 데이터를 dto로 변환 검증
		assertThat(reports).isNotNull();
		assertThat(reports.getTotalElements()).isEqualTo(3);
		assertThat(reports.getTotalElements()).isEqualTo(3);

		// Mock 객체가 실제 예상한 파라미터로 호출되었는지?
		verify(aiDescriptRepository).searchLogsByCondition(any(AiSearchCondition.class), eq(pageable));

	}

	@Test
	@DisplayName("조건에 따른 검색 실패해도 결과 성공? : updatedAt이 들어온 경우")
	void LogSearchCreatedAtTest() {
		Long ownerId = 1L;
		UUID restaurantId = UUID.randomUUID();
		User owner = createUser(ownerId);
		Restaurant restaurant = createRestaurant(restaurantId);

		AiSearchCondition condition = AiSearchCondition.builder()
			.ownerId(ownerId)
			.restaurantId(restaurantId)
			.isSuccess(false)
			.build();
		List<AiDescriptionLog> logs = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			UUID menuId = UUID.randomUUID();
			Menu menu = createMenu(menuId);
			AiDescriptionLog log = AiDescriptionLog.create(
				owner, restaurant, menu, "prompt", "txt", "response"
			);
			logs.add(log);
		}
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<AiDescriptionLog> page = new PageImpl<>(logs, pageable, logs.size());

		given(aiDescriptRepository.searchLogsByCondition(any(AiSearchCondition.class), eq(pageable))).willReturn(page);

		// when
		Page<AiLogSummaryResponseDto> reports = aiDescriptionService.searchLogs(condition, pageable);
		// RuntimeException exception = assertThrows(IllegalArgumentException.class, () -> {
		// 	aiDescriptRepository.searchLogsByCondition(condition, pageable);
		// });

		// 에러를 뱉어내는게 아니고 isSuccess(false)로 인해 데이터 1건만 조회되고 나머지는 조회되지 않음
		// assertThat(reports).isEmpty();

		assertThat(reports).isNotEmpty();
		assertThat(reports.getTotalElements()).isEqualTo(4);
	}

	private User createUser(Long userId) {
		User user = User.builder()
			.username("test")
			.build();
		ReflectionTestUtils.setField(user, "id", userId);
		return user;
	}
	private Restaurant createRestaurant(UUID restaurantId) {
		Restaurant restaurant = Restaurant.builder()
			.name("testRestaurant")
			.build();
		ReflectionTestUtils.setField(restaurant, "id", restaurantId);
		return restaurant;
	}
	private Menu createMenu(UUID menuId) {
		Menu menu = Menu.builder()
			.name("testMenu")
			.build();
		ReflectionTestUtils.setField(menu, "id", menuId);
		return menu;
	}



}
