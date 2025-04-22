package group.practice.configs.aspects.response;

import group.practice.*;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.*;
import org.springframework.core.*;
import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.http.server.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.*;

@Slf4j
@RestControllerAdvice(
        basePackageClasses = Application.class
)
public class ResponseBodyWrapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body, MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response
    ) {

        // ApiResponse 타입이 반환되면 처리 후 반환
        // 참고 : https://colabear754.tistory.com/231

        if (body instanceof ApiResponse apiResponse) {

            apiResponse.setRequestUri(request.getURI());

            if (apiResponse instanceof ErrorResponse errorResponse) {
                log.warn(
                        "Responding handled error : {} - {}",
                        errorResponse.getStatusCode(), errorResponse.getMessage()
                );
            }

            HttpStatus status = HttpStatus.resolve(apiResponse.getStatusCode());

            if (status == null) {
                log.warn(
                        "Failed to resolve status code : {} - {}",
                        apiResponse.getStatusCode(), apiResponse.getMessage()
                );

                int statusCode = apiResponse.getStatusCode();
                HttpServletResponse servletResponse =
                        ((ServletServerHttpResponse) response).getServletResponse();
                servletResponse.setStatus(statusCode);

                log.warn("Status code has been set manually to : {}", statusCode);
            } else {
                response.setStatusCode(status);
            }

            return apiResponse.toBody();
        }

        // 현재 String 을 controller 에서 그냥 반환하면 error 발생함.
        // Spring 의 StringHttpMessageConverter 때문.
        // 아래 글처럼 해결할 수 있긴 한데 REST 에서 String 자체를
        // 반환하는 것은 옳지 않아 보여 해결 안함.
        // 참고 : https://colabear754.tistory.com/232

        log.warn("""
                        Unexpected response body instance encountered : {}
                        SelectedContentType : {}
                        SelectedConverterType : {}
                        Request URI : {}
                        """.trim(),
                body.getClass().getSimpleName(),
                selectedContentType, selectedConverterType,
                request.getURI()
        );

        return body;
    }
}
