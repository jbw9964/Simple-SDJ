package group.practice.exceptions.controller;

import group.practice.exceptions.*;

public class InvalidIdRequest extends CustomException {

    protected InvalidIdRequest() {
        super(ExceptionStatus.INVALID_ID_REQUEST);
    }

    public static InvalidIdRequest of() {
        return new InvalidIdRequest();
    }
}
