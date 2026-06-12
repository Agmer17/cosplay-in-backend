package com.agmerrizky.cosplayin.common.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.agmerrizky.cosplayin.common.api.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<ErrorResponse<String>> handleNotFoundException(NotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ErrorResponse.<String>builder()
                                                .error(ex.getMessage())
                                                .build());
        }

        @ExceptionHandler(FatalError.class)
        public ResponseEntity<ErrorResponse<String>> handleFatalError(FatalError ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ErrorResponse.<String>builder()
                                                .error(ex.getMessage())
                                                .build());
        }

        @ExceptionHandler(BadRequestsException.class)
        public ResponseEntity<ErrorResponse<String>> handleBadRequest(BadRequestsException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ErrorResponse.<String>builder()
                                                .error(ex.getMessage())
                                                .build());
        }

        @ExceptionHandler(ConflictDataException.class)
        public ResponseEntity<ErrorResponse<String>> handleConflictData(ConflictDataException ex) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(ErrorResponse.<String>builder()
                                                .error(ex.getMessage())
                                                .build());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse<Map<String, String>>> handleInvalidMethodArgument(
                        MethodArgumentNotValidException ex) {
                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult()
                                .getFieldErrors()
                                .forEach(error -> errors.put(
                                                error.getField(),
                                                error.getDefaultMessage()));

                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(ErrorResponse.<Map<String, String>>builder()
                                                .error(errors)
                                                .build());
        }

        @ExceptionHandler(UnathorizedAccessException.class)
        public ResponseEntity<ErrorResponse<String>> handleUnauthorizedException(
                        UnathorizedAccessException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ErrorResponse.<String>builder()
                                                .error(ex.getMessage())
                                                .build());
        }

        @ExceptionHandler(ForbiddenAccessException.class)
        public ResponseEntity<ErrorResponse<String>> handleForbiddenAccessException(
                        ForbiddenAccessException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ErrorResponse.<String>builder()
                                                .error(ex.getMessage())
                                                .build());
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse<String>> handleUncaughtException(Exception ex) {

                ex.printStackTrace();

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(
                                                ErrorResponse.<String>builder()
                                                                .error("something wrong with the server, heres the stack trace o debug in dev mode : "
                                                                                + ex.getMessage())
                                                                .build());
        }
}
