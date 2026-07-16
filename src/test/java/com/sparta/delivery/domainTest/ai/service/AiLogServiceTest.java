package com.sparta.delivery.domainTest.ai.service;
import static org.assertj.core.api.Assertions.*;
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
import com.sparta.delivery.domain.ai.service.descript.AiDescriptionService;
import com.sparta.delivery.domain.menu.entity.Menu;
import com.sparta.delivery.domain.menu.repository.MenuRepository;
import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.domain.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AiLogServiceTest {
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

	// 지금 서비스코드 테스트는 변환 및 위임 검증 여부에 집중
	@Test
	@DisplayName("로그 검색 시 레포 호출 및 dto 변환 검증")
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
		// willReturn: 조건에 상관없이 Repo의 searchLogsByCondition이 호출되면 내가 만든 가짜 page객체를 반환

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
	@DisplayName("조건에 따른 검색 실패해도 결과 성공? : 잘못된 테스트코드 예시")
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

		// 에러를 뱉어내는게 아니고 isSuccess(false)로 queryDsl의 메서드를 통해서 false로 변환됨
		// 그러면 BooleanException은 null값을 허용하니깐  isSuccess == false, start&endDate == null
		// 입력된후에 owner,menu,restaurant만으로 채워진 테이블 4행이 조회됨
		// 근데 생각해보니 Mock환경이므로
		// assertThat(reports).isEmpty(); == False

		assertThat(reports).isNotEmpty();
		assertThat(reports.getTotalElements()).isEqualTo(4);
	}

	@Test
	@DisplayName("자세한 검색시 레포 호출 및 dto 변환 검증")
	void SearchDetailsTest() {
		AiSearchCondition condition = AiSearchCondition.builder()
			.ownerId(1L)
			.build();
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

		AiDescriptionLog log = AiDescriptionLog.create(createUser(1L), createRestaurant(UUID.randomUUID()),
			createMenu(UUID.randomUUID()), "p", "t", "r");

		Page<AiDescriptionLog> expectedPage = new PageImpl<>(List.of(log), pageable, 1);

		// Repo -> entity
		given(aiDescriptRepository.searchLogsByCondition(any(AiSearchCondition.class), eq(pageable))).willReturn(expectedPage);
		// when
		Page<AiLogSummaryResponseDto> reports = aiDescriptionService.searchLogs(condition, pageable);
		// then
		// Dto transform?
		assertThat(reports.getContent()).hasSize(1);
		assertThat(reports.getContent().get(0)).isInstanceOf(AiLogSummaryResponseDto.class);

		// Repo method call 제대로?
		verify(aiDescriptRepository, times(1)).searchLogsByCondition(any(AiSearchCondition.class), eq(pageable));
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
