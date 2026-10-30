package com.myclawd.platform.web;

/** Thrown when a requested record does not exist; rendered as HTTP 404. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String what, Object id) {
        super(what + " " + id + " not found");
    }
}
