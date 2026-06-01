package ch.bbw.m183.vulnerapp.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, Object> errors = new HashMap<>();
		errors.put("status", HttpStatus.BAD_REQUEST.value());
		errors.put("error", "Validation Error");
		
		Map<String, String> fieldErrors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->
			fieldErrors.put(error.getField(), error.getDefaultMessage())
		);
		errors.put("errors", fieldErrors);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}
}
