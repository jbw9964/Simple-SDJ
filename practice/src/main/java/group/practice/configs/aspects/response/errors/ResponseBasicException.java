package group.practice.configs.aspects.response.errors;

import static group.practice.configs.aspects.response.errors.Utils.*;

import group.practice.configs.aspects.response.*;
import group.practice.configs.aspects.response.ErrorResponse;
import group.practice.configs.aspects.response.ErrorResponse.*;
import java.util.*;
import lombok.extern.slf4j.*;
import org.springframework.web.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.*;

@Slf4j
@RestControllerAdvice
public class ResponseBasicException {

    private static final String ON_METHOD_NOT_SUPPORTED = """
            현 요청은 [HTTP : %s] 를 지원하지 않습니다.
            """;

    private static final String ON_NO_STATIC_RESOURCE_FOUND = """
            요청과 관련된 자원을 찾을 수 없습니다. 요청 URI 를 확인해 주세요.
            """;

    private static final String ON_QUERY_PARAM_MISSING = """
            요청에 필요한 쿼리 파라미터가 존재하지 않습니다.
            """;


    // method not supported exception
    @ExceptionHandler(exception = HttpRequestMethodNotSupportedException.class)
    public ErrorResponse responseMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e
    ) {
        String message = String.format(
                ON_METHOD_NOT_SUPPORTED,
                e.getMethod()
        );

        String supportedMethods
                = "허용된 HTTP METHOD : " +
                  Arrays.toString(e.getSupportedMethods());

        return toResponse(Code405::of, supportedMethods, message);
    }


    // no static resource found : invalid uri
    @ExceptionHandler(exception = NoResourceFoundException.class)
    public ErrorResponse responseNoStaticResourceFoundException(
            NoResourceFoundException e
    ) {
        return toResponse(Code404::of, e.getMessage(),
                ON_NO_STATIC_RESOURCE_FOUND);
    }

    @ExceptionHandler(exception = MissingServletRequestParameterException.class)
    public ErrorResponse responseMissingServletRequestParameterException(
            MissingServletRequestParameterException e
    ) {

        String showMissingParam = String.format("""
                파라미터 [%s] 가 존재하지 않습니다.
                """.trim(), e.getParameterName());

        return toResponse(Code400::of, showMissingParam,
                ON_QUERY_PARAM_MISSING);
    }
}
