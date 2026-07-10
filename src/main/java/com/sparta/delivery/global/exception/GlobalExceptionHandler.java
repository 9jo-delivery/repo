package com.sparta.delivery.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// 1. 400 Bad Request (잘못된 요청 - 파라미터 누락, 타입 오류 등)
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST) // 400
			.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
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
}
