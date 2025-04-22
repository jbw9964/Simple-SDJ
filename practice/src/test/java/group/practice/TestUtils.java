package group.practice;

import static org.assertj.core.api.Assertions.*;

import java.util.function.*;
import lombok.extern.slf4j.*;

@Slf4j
public abstract class TestUtils {

    public static void assertExist(
            Long id, Function<Long, Boolean> func
    ) {
        assertThat(func.apply(id)).isTrue();
    }

    public static void assertNotExist(
            Long id, Function<Long, Boolean> func
    ) {
        assertThat(func.apply(id)).isFalse();
    }

    @SafeVarargs
    public static <T> void assertThrows(
            Class<? extends Exception> exClass,
            Function<T, ?> func, T... params
    ) {
        for (T param : params) {
            assertThatThrownBy(() -> func.apply(param))
                    .isInstanceOf(exClass);
        }

        logExceptionPass(exClass.getSimpleName());
    }

    private static void logExceptionPass(String exClass) {
        log.info("Exception {} passed.", exClass);
    }
}
