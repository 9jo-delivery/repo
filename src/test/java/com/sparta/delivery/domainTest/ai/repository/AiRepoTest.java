package com.sparta.delivery.domainTest.ai.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import com.sparta.delivery.domain.ai.dto.AiLogDto.AiSearchCondition;
import com.sparta.delivery.domain.ai.entity.AiDescriptionLog;
import com.sparta.delivery.domain.ai.repository.AiDescriptRepository;
import com.sparta.delivery.domain.menu.entity.Menu;
import com.sparta.delivery.domain.menu.repository.MenuRepository;
import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.domain.region.repository.RegionRepository;
import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.domain.restaurant.entity.RestaurantCategory;
import com.sparta.delivery.domain.restaurant.repository.RestaurantCategoryRepository;
import com.sparta.delivery.domain.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import com.sparta.delivery.global.common.Enums;
import com.sparta.delivery.global.config.config.QueryDslConfig;

@DataJpaTest
@ActiveProfiles("test")
@Import(QueryDslConfig.class)
@DisplayName("AiLog 레포 동적 쿼리 테스트")
class AiRepoTest {

	@Autowired
	private AiDescriptRepository aiDescriptRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private RestaurantCategoryRepository restaurantCategoryRepository;
	@Autowired
	private RegionRepository regionRepository;

	@Test
	@DisplayName("조건별 쿼리 통과 테스트")
	void searchConditionPass() {

		User owner = createUser();
		Restaurant restaurant = createRestaurant(owner);
		Menu menu = createMenu(restaurant);

		aiDescriptRepository.save(AiDescriptionLog.create(owner, restaurant, menu, "prompt1", "txt", "res1"));

		AiSearchCondition condition = AiSearchCondition.builder()
			.isSuccess(true)
			.startDate(LocalDate.now())
			.endDate(LocalDate.now().plusDays(1))
			.build();
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

		// when
		Page<AiDescriptionLog> result = aiDescriptRepository.searchLogsByCondition(condition, pageable);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getPrompt()).isEqualTo("prompt1");
	}

	private User createUser() {
		return userRepository.save(User.builder()
			.username("testUser")
			.password("password123")
			.name("테스트유저")
			.phone("010-1234-5678")
			.role(Enums.UserRole.OWNER) // Enum 사용
			.build());
	}

	private Restaurant createRestaurant(User owner) {
		RestaurantCategory category = createRestaurantCategory();
		Region region = regionRepository.save(Region.builder().name("서울").regionType(Enums.RegionType.SIGUNGU).isServiceAvailable(true).build());

		return restaurantRepository.save(Restaurant.builder()
			.owner(owner)
			.category(category)
			.region(region)
			.name("테스트 식당")
			.description("맛있는 식당입니다")
			.phone("02-123-4567")
			.address("서울시 강남구")
			.businessNumber("123-45-67890")
			.minOrderAmount(10000)
			.deliveryFee(3000)
			.build());
	}

	private Menu createMenu(Restaurant restaurant) {
		return menuRepository.save(Menu.builder()
			.restaurant(restaurant)
			.name("테스트 메뉴")
			.price(15000)
			.description("테스트용 맛있는 메뉴")
			.isHidden(false)
			.isSoldOut(false)
			.build());
	}

	private RestaurantCategory createRestaurantCategory() {
		return restaurantCategoryRepository.save(RestaurantCategory.builder()
			.name("한식")
			.description("dd")
			.sortOrder(2)
			.isActive(true)
			.build()
		);
	}
}
