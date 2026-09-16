package staffs.leaverequestapp;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import staffs.leaverequestapp.leave.domain.exceptions.InsufficientLeaveBalanceException;
import staffs.leaverequestapp.leave.domain.exceptions.LeaveRequestCannotBeCancelledByProxyException;
import staffs.leaverequestapp.leave.domain.exceptions.LeaveRequestCannotBeRejectedException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            InsufficientLeaveBalanceException.class,
            LeaveRequestCannotBeCancelledByProxyException.class,
            LeaveRequestCannotBeRejectedException.class
    })
    public ResponseEntity<Map<String, Object>> handleNotFoundExceptions(RuntimeException ex) {
        // Note: You might want to consider changing this to HttpStatus.BAD_REQUEST (400)
        // in the future, as business rule violations aren't technically "missing" resources!
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = ex.getMessage();
        Map<String, String> validationErrors = null;

        if (ex instanceof ResponseStatusException rse) {
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            message = rse.getReason();
        }
        else if (ex instanceof HttpMessageNotReadableException hmnre) {
            status = HttpStatus.BAD_REQUEST;
            if (hmnre.getRootCause() instanceof IllegalArgumentException iae) {
                validationErrors = Map.of("errorReason", iae.getMessage());
            } else {
                validationErrors = Map.of("errorReason", "Malformed JSON request payload");
            }
        }
        else if (ex instanceof MethodArgumentNotValidException manve) {
            status = HttpStatus.BAD_REQUEST;
            message = "Validation failed for one or more fields";
            validationErrors = manve.getBindingResult().getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            error -> Objects.requireNonNullElse(error.getDefaultMessage(), "Invalid value"),
                            (existing, replacement) -> existing
                    ));
        }
        else if (ex instanceof ConstraintViolationException cve) {
            status = HttpStatus.BAD_REQUEST;
            message = "Database constraint validation failed.";
            validationErrors = cve.getConstraintViolations().stream()
                    .collect(Collectors.toMap(
                            violation -> violation.getPropertyPath().toString(),
                            ConstraintViolation::getMessage
                    ));
        }
        else if (ex instanceof DataIntegrityViolationException) {
            status = HttpStatus.BAD_REQUEST;
            message = "A duplicate record already exists";
        }
        else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
            message = "Validation failed for one or more fields";
            validationErrors = Map.of("errorReason", ex.getMessage());
        }
        // ADDED: Handles invalid UUIDs (Fixes the GET endpoint test)
        else if (ex instanceof MethodArgumentTypeMismatchException matme) {
            status = HttpStatus.BAD_REQUEST;
            message = "Invalid parameter format for: " + matme.getName();
            validationErrors = Map.of("errorReason", "Failed to convert value to required type.");
        }

        else if (ex instanceof NoResourceFoundException) {
            status = HttpStatus.NOT_FOUND;
            message = "The requested endpoint or resource does not exist.";
        }

        return createErrorResponse(status, message, validationErrors);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(
            HttpStatus status,
            String message,
            Map<String, String> validationErrors
    ) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", status.value());
        responseBody.put("error", status.getReasonPhrase());
        responseBody.put("message", Objects.requireNonNullElse(message, "No message provided"));
        responseBody.put("timestamp", Instant.now().toString());

        if (validationErrors != null && !validationErrors.isEmpty()) {
            responseBody.put("errors", validationErrors);
        }

        return ResponseEntity.status(status).body(responseBody);
    }
}