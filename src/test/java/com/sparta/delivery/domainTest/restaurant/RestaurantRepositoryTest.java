package com.sparta.delivery.domainTest.restaurant;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.domain.restaurant.entity.RestaurantCategory;
import com.sparta.delivery.domain.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.global.common.Enums;
import com.sparta.delivery.global.config.config.QueryDslConfig;

@DataJpaTest
@Import(QueryDslConfig.class)
class RestaurantRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("QueryDSL 가게 검색 - 카테고리만 입력하면 해당 카테고리 가게만 조회")
    void searchRestaurants_FilterByCategoryOnly() {
        // given
        User owner = saveOwner("owner1");
        Region region = saveRegion("서울 강남구");
        RestaurantCategory chicken = saveCategory("치킨");
        RestaurantCategory pizza = saveCategory("피자");
        Restaurant chickenRestaurant = saveRestaurant(owner, chicken, region, "맛있는 치킨집", 4.5, 10L);
        saveRestaurant(owner, pizza, region, "맛있는 피자집", 4.9, 20L);
        em.flush();
        em.clear();

        // when
        Page<Restaurant> result = restaurantRepository.searchRestaurants(
                chicken.getId(),
                null,
                null,
                "",
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(chickenRestaurant.getId());
    }

    @Test
    @DisplayName("QueryDSL 가게 검색 - 카테고리와 이름 일부를 함께 필터링")
    void searchRestaurants_FilterByCategoryAndName() {
        // given
        User owner = saveOwner("owner2");
        Region region = saveRegion("서울 서초구");
        RestaurantCategory bunsik = saveCategory("분식");
        Restaurant expected = saveRestaurant(owner, bunsik, region, "강남 떡볶이", 4.7, 30L);
        saveRestaurant(owner, bunsik, region, "강남 김밥", 4.8, 40L);
        em.flush();
        em.clear();

        // when
        Page<Restaurant> result = restaurantRepository.searchRestaurants(
                bunsik.getId(),
                null,
                null,
                "떡볶이",
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "averageRating"))
        );

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(expected.getId());
        assertThat(result.getContent().get(0).getName()).contains("떡볶이");
    }

    private User saveOwner(String username) {
        User owner = User.builder()
                .username(username)
                .password("password")
                .name(username)
                .phone("010-0000-0000")
                .role(Enums.UserRole.OWNER)
                .build();
        em.persist(owner);
        return owner;
    }

    private Region saveRegion(String name) {
        Region region = Region.create(null, name, Enums.RegionType.SIGUNGU, true);
        em.persist(region);
        return region;
    }

    private RestaurantCategory saveCategory(String name) {
        RestaurantCategory category = RestaurantCategory.builder()
                .name(name)
                .description(name + " 카테고리")
                .sortOrder(1)
                .isActive(true)
                .build();
        em.persist(category);
        return category;
    }

    private Restaurant saveRestaurant(
            User owner,
            RestaurantCategory category,
            Region region,
            String name,
            double averageRating,
            Long reviewCount
    ) {
        Restaurant restaurant = Restaurant.create(
                owner,
                category,
                region,
                name,
                name + " 설명",
                "02-1234-5678",
                "서울시 테스트로 1",
                "1층",
                UUID.randomUUID().toString(),
                10000,
                3000
        );
        restaurant.updateRatingAndCount(averageRating, reviewCount);
        em.persist(restaurant);
        return restaurant;
    }
}
