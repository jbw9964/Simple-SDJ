package group.practice.configs.aspects.response.errors;

import static group.practice.configs.aspects.response.errors.Utils.*;

import group.practice.configs.aspects.response.*;
import group.practice.configs.aspects.response.ErrorResponse.*;
import group.practice.exceptions.*;
import java.util.*;
import lombok.extern.slf4j.*;
import org.springframework.validation.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice
public class ResponseException {

    private static final String ON_VALIDATION_FAILED = """
            요청 파라미터가 올바르지 않습니다.
            """;

    private static final String ON_UNEXPECTED_EX = """
            서버에 알 수 없는 에러가 발생했습니다.
            """;

    private static String buildErrorMessage(FieldError fieldError) {
        return String.format(
                "%s : %s",
                fieldError.getField(), fieldError.getDefaultMessage()
        );
    }

    // validation errors
    @ExceptionHandler(exception = MethodArgumentNotValidException.class)
    public ErrorResponse responseRequestDtoBindException(
            MethodArgumentNotValidException e
    ) {

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> fieldErrorMessages = fieldErrors.stream()
                .map(ResponseException::buildErrorMessage)
                .toList();

        return toResponse(Code400::of, fieldErrorMessages, ON_VALIDATION_FAILED);
    }

    // covered exceptions
    @ExceptionHandler(exception = CustomException.class)
    public ErrorResponse responseCustomException(CustomException e) {
        int statusCode = e.getStatusCode();
        String message = e.getMessage();

        return toResponse(ErrorResponse::of, statusCode, null, message);
    }

    // unexpected exceptions
    @ExceptionHandler(exception = Throwable.class)
    public ErrorResponse responseUnexpectedException(Throwable e) {
        log.warn(
                "Handled unexpected exception : {}",
                e.getClass().getSimpleName(), e
        );
        return toResponse(Code500::of, e.getMessage(), ON_UNEXPECTED_EX);
    }
}
