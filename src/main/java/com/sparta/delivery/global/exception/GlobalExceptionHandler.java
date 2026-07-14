package com.sparta.delivery.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// 400 Bad Request (유효성 검증 실패 및 파라미터 누락)
	// @Valid 유효성 검증 실패 시
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage));
	}

	// 지원하지 않는 데이터 형식, JSON 파싱 에러 등
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "잘못된 JSON 형식의 요청입니다."));
	}

	// 401 Unauthorized
	@ExceptionHandler(InsufficientAuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(InsufficientAuthenticationException ex) {
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰입니다."));
	}

	// 403 Forbidden (권한 부족)
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
		return ResponseEntity
			.status(HttpStatus.FORBIDDEN)
			.body(new ErrorResponse(HttpStatus.FORBIDDEN.value(), "해당 요청에 대한 접근 권한이 없습니다."));
	}


	// 특정 커스텀 예외 처리 (404 Not Found)
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
		ErrorResponse errorResponse = new ErrorResponse(
			HttpStatus.NOT_FOUND.value(),
			ex.getMessage()
		);
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	// 5. 405 Method Not Allowed (지원하지 않는 HTTP 메서드로 요청했을 때, 예: POST인데 GET으로 보냄)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class) // Spring 내장 예외
	public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
		return ResponseEntity
			.status(HttpStatus.METHOD_NOT_ALLOWED) // 405
			.body(new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), "지원하지 않는 HTTP 메서드입니다."));
	}

	//409 Conflict (비즈니스 규칙 위반 - 상태 전이 불가, 시간 초과 등)
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ErrorResponse> handleConflict(IllegalStateException ex) {
		return ResponseEntity
				.status(HttpStatus.CONFLICT) // 409
				.body(new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage()));
	}

	@ExceptionHandler(Exception.class) // 위에서 걸러지지 않은 모든 예외 처리
	public ResponseEntity<ErrorResponse> handleAllException(Exception ex) {
		// 로깅을 남겨서 서버 콘솔에서 개발자가 확인할 수 있게 함
		log.error("Unhandled Exception: ", ex);

		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요."));
	}

}
