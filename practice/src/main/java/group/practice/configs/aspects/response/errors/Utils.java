package group.practice.configs.aspects.response.errors;

import group.practice.configs.aspects.response.*;

public abstract class Utils {

    public static <R extends ApiResponse> R toResponse(Singlet<R> singlet, Object data) {
        return singlet.build(data);
    }

    public static <R extends ApiResponse> R toResponse(
            Doublet<R> doublet, Object data, String message
    ) {
        return doublet.build(data, message.trim());
    }

    public static <R extends ApiResponse> R toResponse(
            Triplet<R> triplet, int statusCode, Object data, String message
    ) {
        return triplet.build(statusCode, data, message.trim());
    }

    @FunctionalInterface
    public interface Singlet<R extends ApiResponse> {

        R build(Object data);
    }

    @FunctionalInterface
    public interface Doublet<R extends ApiResponse> {

        R build(Object data, String message);
    }

    @FunctionalInterface
    public interface Triplet<R extends ApiResponse> {

        R build(int statusCode, Object data, String message);
    }
}
