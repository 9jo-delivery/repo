package com.sparta.delivery.domain.review.repository;

import static com.sparta.delivery.domain.review.entity.QReview.*;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.delivery.domain.review.dto.ReviewSearchCondition;
import com.sparta.delivery.domain.review.entity.Review;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Review> searchRestaurantReviews(UUID restaurantId, ReviewSearchCondition condition, Pageable pageable) {
		// 1. (실제 데이터) 조건에 맞는 리뷰 데이터 목록을 가져와서 reviews에 담음
		List<Review> reviews = queryFactory
			.selectFrom(review)
			.where(
				review.restaurant.id.eq(restaurantId), // 기본 필수 조건: 이 식당의 리뷰여야 함
				ratingEq(condition.getRating())        // 동적 조건: 별점 필터 조건
			)
			.offset(pageable.getOffset())  // 페이징: 어디서부터 시작할지 문제점: 1000번대부터 10개 1~1000 오프셋리밋 개선방안 있음 -> 개선방안 찾아보기
			.limit(pageable.getPageSize()) // 페이징: 몇 개를 가져올지
			.orderBy(review.createdAt.desc())
			.fetch(); // 이거 호출하면 ListReview형태로 반환함

		// 2. pageable(실제 데이터 포장) <--이미 컨트롤러에서 받음

		// 3. 받아온 모든 포장 데이터 조건식 등으로 조립
		// 실제로는 메서드 호출 받을때 작동
		JPAQuery<Long> countQuery = queryFactory
			.select(review.count())
			.from(review)
			.where(
				review.restaurant.id.eq(restaurantId),
				ratingEq(condition.getRating())
			);

		return PageableExecutionUtils.getPage(reviews, pageable, countQuery::fetchOne);
	}

	private BooleanExpression ratingEq(Integer rating) {
		// 별점 검색 조건이 없으면(null) null을 반환
		// QueryDSL은 where 절 안에 null이 들어오면 해당 조건을  무시
		if (rating == null) {
			return null;
		}
		// 조건이 있으면 "리뷰의 평점이 입력받은 평점과 같은가?" 라는 조건을 반환
		return review.rating.eq(rating);
	}
}
