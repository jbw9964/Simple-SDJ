package group.practice;

import group.practice.configs.aspects.response.*;
import group.practice.exceptions.*;
import java.net.*;
import lombok.extern.slf4j.*;
import org.springframework.test.web.servlet.request.*;

@Slf4j
@SuppressWarnings({"SameParameterValue", "unused"})
public abstract class MvcTestUtils {

    public static ErrorResponse buildResponseFromEx(
            String uri, CustomException e
    ) {
        int statusCode = e.getStatusCode();
        String message = e.getMessage();

        var resp = ErrorResponse.of(statusCode, null, message);
        resp.setRequestUri(URI.create(uri));

        return resp;
    }

    @FunctionalInterface
    public interface UriToRequestBuilder {

        UriToRequestBuilder GET
                = MockMvcRequestBuilders::get;
        UriToRequestBuilder PUT
                = MockMvcRequestBuilders::put;
        UriToRequestBuilder POST
                = MockMvcRequestBuilders::post;
        UriToRequestBuilder HEAD
                = MockMvcRequestBuilders::head;
        UriToRequestBuilder DELETE
                = MockMvcRequestBuilders::delete;
        UriToRequestBuilder OPTIONS
                = MockMvcRequestBuilders::options;
        UriToRequestBuilder PATCH
                = MockMvcRequestBuilders::patch;

        MockHttpServletRequestBuilder build(URI uri);
    }


    public record Request(
            Object request, Object response,
            Exception exception
    ) {

        public static Request of(Object request, Object response) {
            return new Request(request, response, null);
        }

        public static Request of(Object request, Exception exception) {
            return new Request(request, null, exception);
        }
    }
}
