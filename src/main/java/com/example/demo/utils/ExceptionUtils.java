package com.example.demo.utils;

public class ExceptionUtils {

    public static Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable.getCause();
        return (cause != null && cause != throwable) ? getRootCause(cause) : throwable;
    }
}