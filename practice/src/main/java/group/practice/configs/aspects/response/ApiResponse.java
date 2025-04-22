package group.practice.configs.aspects.response;

import java.net.*;
import java.time.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@ToString(of = {"statusCode", "message", "requestUri"})
public abstract class ApiResponse {

    @Getter
    private final int statusCode;

    @Getter
    private final Object data;

    @Getter
    private final String message;

    private URI requestUri;

    protected ApiResponse(int statusCode, Object data, String message) {
        this.statusCode = statusCode;
        this.data = data;
        this.message = message;
    }

    public ApiResponse setRequestUri(String uri) {
        try {
            this.requestUri = new URI(uri);
        } catch (Exception e) {
            log.warn(
                    "Unexpected error has been occurred " +
                    "while setting response uri - {}", uri, e
            );
        }
        return this;
    }

    public ApiResponse setRequestUri(URI uri) {
        this.requestUri = uri;
        return this;
    }

    public Body toBody() {
        return Body.of(this);
    }

    public record Body(
            int statusCode,
            LocalDateTime timestamp,
            URI request,
            String message,
            Object data
    ) {

        private static Body of(ApiResponse response) {
            return new Body(
                    response.statusCode, LocalDateTime.now(),
                    response.requestUri, response.message,
                    response.data
            );
        }
    }
}
