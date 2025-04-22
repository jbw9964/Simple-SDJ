package group.practice;

import static group.practice.MvcTestUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.*;
import group.practice.configs.aspects.response.*;
import group.practice.configs.aspects.response.ApiResponse.*;
import group.practice.services.*;
import java.net.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.*;
import org.springframework.test.web.servlet.*;

@Slf4j
@WebMvcTest
@SuppressWarnings({"SameParameterValue", "unused"})
public abstract class ControllerTestSupport {

    private static final String REQUEST_URI_PREFIX
            = "http://localhost";
    private static final MediaType DEFAULT_MEDIA_TYPE
            = MediaType.APPLICATION_JSON;
    @Getter
    @Autowired
    MockMvc mockMvc;
    @Getter
    @MockitoBean
    JoinService joinService;
    @Getter
    @MockitoBean
    RegisterService registerService;
    @Getter
    @MockitoBean
    ViewService viewService;
    @Getter
    @Autowired
    ObjectMapper objectMapper;

    protected abstract void setupMocks();

    protected ResultActions doRequest(
            UriToRequestBuilder builder,
            String uri, Object requestBody,
            MediaType mediaType
    ) throws Exception {

        URI requestUri = URI.create(uri);
        var request = builder.build(requestUri);

        if (requestBody != null) {
            request.content(toRequestBody(requestBody));
        }

        return mockMvc.perform(request.contentType(mediaType));
    }

    protected ResultActions validateResponse(
            ResultActions result, ApiResponse response,
            boolean checkStatusCode, boolean checkMessage,
            boolean checkRequestUri, boolean checkData,
            boolean printResult
    ) throws Exception {

        Body responseBody = response.toBody();

        int statusCode = responseBody.statusCode();
        String message = responseBody.message();
        String requestUri = REQUEST_URI_PREFIX +
                            responseBody.request().toString();
        Object data = responseBody.data();

        result
                .andExpect(jsonPath("$.timestamp")
                        .isNotEmpty());

        if (checkStatusCode) {
            result
                    .andExpect(status().is(statusCode))
                    .andExpect(jsonPath("$.statusCode")
                            .value(statusCode));
        }

        if (checkMessage) {
            result
                    .andExpect(jsonPath("$.message")
                            .value(message));
        }

        if (checkRequestUri) {
            result
                    .andExpect(jsonPath("$.request")
                            .value(requestUri));
        }

        if (checkData) {
            String actualRespBody = result.andReturn()
                    .getResponse().getContentAsString();
            JsonNode dataNode = objectMapper
                    .readTree(actualRespBody).get("data");
            JsonNode expectedDataNode = objectMapper.readTree(
                    objectMapper.writeValueAsString(data)
            );

            assertThat(dataNode).isEqualTo(expectedDataNode);
        }

        log.info(
                "Validated response: {} on uri {}",
                response, checkRequestUri
        );

        if (printResult) {
            result.andDo(print());
        }

        return result;
    }

    protected ResultActions assertResponse(
            UriToRequestBuilder builder,
            String uri, Object requestBody,
            ApiResponse response
    ) throws Exception {

        return assertResponse(
                builder, uri, requestBody,
                DEFAULT_MEDIA_TYPE, response,
                true, true,
                true, false
        );
    }

    protected ResultActions assertResponse(
            UriToRequestBuilder builder,
            String uri, Object requestBody,
            MediaType mediaType, ApiResponse response,
            boolean checkMessage, boolean checkRequestUri,
            boolean checkData, boolean printResult
    ) throws Exception {

        ResultActions result = this.doRequest(
                builder, uri, requestBody, mediaType
        );

        return validateResponse(result, response,
                true, checkMessage,
                checkRequestUri, checkData,
                printResult);
    }

    private String toRequestBody(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            log.error(
                    "Error has been occurred while serializing " +
                    "object to json request body.", e
            );
            throw new RuntimeException(e);
        }
    }

}
