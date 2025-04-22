package group.practice.configs.aspects.response;

public class ErrorResponse extends ApiResponse {

    private ErrorResponse(int statusCode, Object data) {
        super(statusCode, data, "에러 발생");
    }

    private ErrorResponse(int statusCode, Object data, String message) {
        super(statusCode, data, message);
    }

    public static ErrorResponse of(int statusCode, Object data, String message) {
        return new ErrorResponse(statusCode, data, message);
    }

    public static class Code400 extends ErrorResponse {

        private Code400(Object data) {
            super(400, data);
        }

        private Code400(Object data, String message) {
            super(400, data, message);
        }

        public static Code400 of(Object data) {
            return new Code400(data);
        }

        public static Code400 of(Object data, String message) {
            return new Code400(data, message);
        }
    }

    public static class Code401 extends ErrorResponse {

        private Code401(Object data) {
            super(401, data);
        }

        private Code401(Object data, String message) {
            super(401, data, message);
        }

        public static Code401 of(Object data) {
            return new Code401(data);
        }

        public static Code401 of(Object data, String message) {
            return new Code401(data, message);
        }
    }

    public static class Code402 extends ErrorResponse {

        private Code402(Object data) {
            super(402, data);
        }

        private Code402(Object data, String message) {
            super(402, data, message);
        }

        public static Code402 of(Object data) {
            return new Code402(data);
        }

        public static Code402 of(Object data, String message) {
            return new Code402(data, message);
        }
    }

    public static class Code403 extends ErrorResponse {

        private Code403(Object data) {
            super(403, data);
        }

        private Code403(Object data, String message) {
            super(403, data, message);
        }

        public static Code403 of(Object data) {
            return new Code403(data);
        }

        public static Code403 of(Object data, String message) {
            return new Code403(data, message);
        }
    }

    public static class Code404 extends ErrorResponse {

        private Code404(Object data) {
            super(404, data);
        }

        private Code404(Object data, String message) {
            super(404, data, message);
        }

        public static Code404 of(Object data) {
            return new Code404(data);
        }

        public static Code404 of(Object data, String message) {
            return new Code404(data, message);
        }
    }

    public static class Code405 extends ErrorResponse {

        private Code405(Object data) {
            super(405, data);
        }

        private Code405(Object data, String message) {
            super(405, data, message);
        }

        public static Code405 of(Object data) {
            return new Code405(data);
        }

        public static Code405 of(Object data, String message) {
            return new Code405(data, message);
        }
    }

    public static class Code500 extends ErrorResponse {

        private Code500(Object data) {
            super(500, data);
        }

        private Code500(Object data, String message) {
            super(500, data, message);
        }

        public static Code500 of(Object data) {
            return new Code500(data);
        }

        public static Code500 of(Object data, String message) {
            return new Code500(data, message);
        }
    }
}
