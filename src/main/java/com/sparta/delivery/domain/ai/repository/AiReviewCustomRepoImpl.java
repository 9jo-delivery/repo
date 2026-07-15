package com.sparta.delivery.domain.ai.repository;

// Q클래스 인스턴스: 타입 안정성 보장 + 자바코드로 SQL 쿼리 헬퍼
// 타입 안정성이란? 컴파일러는 where("menuId = 1")같은 오타를 못잡아냄
// where(qAiDescriptionLog.menuId.eq(1)) 처럼 자바 객체 접근 방식으로 쓰면,
// 오타가 났을 때 컴파일 에러를 띄워줘서 안전하게 코드 짤수있음
import static com.sparta.delivery.domain.ai.entity.QAiDescriptionLog.aiDescriptionLog;
import static com.sparta.delivery.domain.restaurant.entity.QRestaurant.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.delivery.domain.ai.dto.AiLogDto.AiSearchCondition;
import com.sparta.delivery.domain.ai.entity.AiDescriptionLog;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class AiReviewCustomRepoImpl implements AiReviewCustomRepo {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<AiDescriptionLog> searchLogsByCondition(AiSearchCondition condition , Pageable pageable){

		List<AiDescriptionLog> logs = queryFactory
			.selectFrom(aiDescriptionLog)
			.leftJoin(aiDescriptionLog.restaurant, restaurant).fetchJoin()
			.where(
				restaurantIdEq(condition.getRestaurantId()),
				isSuccessEq(condition.getIsSuccess()),
				whenEq(condition.getStartDate(), condition.getEndDate())
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(aiDescriptionLog.createdAt.desc())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(aiDescriptionLog.count())
			.from(aiDescriptionLog)
			.where(
				restaurantIdEq(condition.getRestaurantId()),
				isSuccessEq(condition.getIsSuccess()),
				whenEq(condition.getStartDate(), condition.getEndDate())
			);
		return PageableExecutionUtils.getPage(logs, pageable, countQuery::fetchOne);
		// 처음에 왜 sortQuery.fetchCount() 쓰면돼는거 아닌가>?
		// () -> sortQuery.fetchCount()
		// "전체 페이지 개수를 계산하기 위한 '진짜 Count 쿼리'는 지금 바로 실행하지 말고,
		// 나중에 정말 필요할 때만 실행해줘"라고 JPA에게 예약(지연 실행)해 두는 문법
		// PageableExecutionUtils.getPage()는 첫 페이지나 마지막 페이지 데이터를 보고,
		// "어? 이거 굳이 DB에 Count 쿼리 안 날려도 전체 개수가 몇 개인지 계산이 나오는데?"
		// 라고 판단되면 세 번째 파라미터로 받은 countQuery::fetchOne을 아예 실행조차 하지 않고 패스

	}

	private BooleanExpression restaurantIdEq(UUID restaurantId) {
		if (restaurantId == null) {
			return null;
		}
		return aiDescriptionLog.restaurant.id.eq(restaurantId);
	}

	private BooleanExpression isSuccessEq(Boolean isSuccess) {
		if (isSuccess == null) {
			return null;
		}
		return aiDescriptionLog.isSuccess.eq(isSuccess);
	}

	private BooleanExpression whenEq(LocalDate startDate, LocalDate endDate) {
		if (startDate == null && endDate == null) {
			return null;
		}
		if (startDate != null && endDate == null) {
			return aiDescriptionLog.createdAt.goe(startDate.atStartOfDay());
		}
		if (startDate == null) {
			return aiDescriptionLog.createdAt.lt(endDate.plusDays(1).atStartOfDay());
		}
		// 조건이 전부 부합 할때 시작과 마지막 종료일의 중간값을 구함
		return aiDescriptionLog.createdAt.between(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
	}

}
