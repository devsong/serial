package com.gzs.learn.serial.exception;

public class SerialException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SerialException() {
        super();
    }

    public SerialException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public SerialException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerialException(String message) {
        super(message);
    }

    public SerialException(Throwable cause) {
        super(cause);
    }

    public SerialException(SerialCode code, String message) {
        super("code:" + code.getCode() + " msg:" + code.getMsg() + message);
    }


    public SerialException(SerialCode code) {
        super("code:" + code.getCode() + " msg:" + code.getMsg());
    }
}
