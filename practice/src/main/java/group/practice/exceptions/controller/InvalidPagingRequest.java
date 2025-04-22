package group.practice.exceptions.controller;

import group.practice.exceptions.*;

public class InvalidPagingRequest extends CustomException {

    public InvalidPagingRequest() {
        super(ExceptionStatus.INVALID_PAGING_REQUEST);
    }

    public static InvalidPagingRequest of() {
        return new InvalidPagingRequest();
    }
}
