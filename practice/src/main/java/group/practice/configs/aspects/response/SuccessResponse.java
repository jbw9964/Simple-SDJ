package group.practice.configs.aspects.response;

public class SuccessResponse extends ApiResponse {

    private SuccessResponse(int statusCode, Object data) {
        super(statusCode, data, "요청 성공");
    }

    private SuccessResponse(int statusCode, Object data, String message) {
        super(statusCode, data, message);
    }

    public static SuccessResponse of(int statusCode, Object data, String message) {
        return new SuccessResponse(statusCode, data, message);
    }

    /**
     * Plain {@code OK}
     */
    public static class Code200 extends SuccessResponse {

        private Code200(Object data) {
            super(200, data);
        }

        private Code200(Object data, String message) {
            super(200, data, message);
        }

        public static Code200 of(Object data) {
            return new Code200(data);
        }

        public static Code200 of(Object data, String message) {
            return new Code200(data, message);
        }
    }

    /**
     * On {@code Created}
     */
    public static class Code201 extends SuccessResponse {

        private Code201(Object data) {
            super(201, data);
        }

        private Code201(Object data, String message) {
            super(201, data, message);
        }

        public static Code201 of(Object data) {
            return new Code201(data);
        }

        public static Code201 of(Object data, String message) {
            return new Code201(data, message);
        }
    }

    /**
     * On {@code Accepted}
     */
    public static class Code202 extends SuccessResponse {

        private Code202(Object data) {
            super(202, data);
        }

        private Code202(Object data, String message) {
            super(202, data, message);
        }

        public static Code202 of(Object data) {
            return new Code202(data);
        }

        public static Code202 of(Object data, String message) {
            return new Code202(data, message);
        }
    }

    /**
     * On {@code Non-Authoritative Information}
     */
    public static class Code203 extends SuccessResponse {

        private Code203(Object data) {
            super(203, data);
        }

        private Code203(Object data, String message) {
            super(203, data, message);
        }

        public static Code203 of(Object data) {
            return new Code203(data);
        }

        public static Code203 of(Object data, String message) {
            return new Code203(data, message);
        }
    }

    /**
     * On {@code No Content}
     */
    public static class Code204 extends SuccessResponse {

        private Code204(Object data) {
            super(204, data);
        }

        private Code204(Object data, String message) {
            super(204, data, message);
        }

        public static Code204 of(Object data) {
            return new Code204(data);
        }

        public static Code204 of(Object data, String message) {
            return new Code204(data, message);
        }
    }

    /**
     * On {@code Reset Content}
     */
    public static class Code205 extends SuccessResponse {

        private Code205(Object data) {
            super(205, data);
        }

        private Code205(Object data, String message) {
            super(205, data, message);
        }

        public static Code205 of(Object data) {
            return new Code205(data);
        }

        public static Code205 of(Object data, String message) {
            return new Code205(data, message);
        }
    }
}
