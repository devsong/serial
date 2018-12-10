package com.gzs.learn.serial.exception;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * 定义统一的异常返回码
 * 
 * @author guanzhisong
 *
 */
@Getter
public enum SerialCode {
    SERIAL_USES_FULL(10001, "serial used full"),

    SERIAL_CREATE_ERROR(10002, "serial create error"),

    SERIAL_MANAGER_ERROR(10003, "serial manager error"),

    SERIAL_ALREADY_EXISTS(10004, "serial name alreadt exists"),

    SERIAL_NAME_LEN_TOO_LONG(10005, "serial name too long"),

    SERIAL_VERSION_ERROR(10006, "serial version must greater than 0"),

    SERIAL_PARTITION_ERROR(10007, "serial partition must greater than 8"),

    SERIAL_STEP_ERROR(10008, "serial step error,step greater than 10000"),

    SERIAL_RANGE_ERROR(10009, "serial range error,max must greater than min");

    int code;
    String msg;

    static Map<Integer, SerialCode> MAPPING = new HashMap<>();

    static {
        for (SerialCode code : values()) {
            MAPPING.put(code.getCode(), code);
        }
    }

    private SerialCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static SerialCode parse(int code) {
        SerialCode c = MAPPING.get(code);
        if (c == null) {
            throw new IllegalArgumentException("unknow serial code:" + code);
        }
        return c;
    }
}
