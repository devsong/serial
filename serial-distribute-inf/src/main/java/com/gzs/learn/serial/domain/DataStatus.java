package com.gzs.learn.serial.domain;

import java.util.HashMap;

import lombok.Getter;

@Getter
public enum DataStatus {
    /**
     * 无
     */
    NONE(0, 0),
    /**
     * 启用
     */
    ENABLE(1, 0),
    /**
     * 禁用
     */
    DISABLE(1, 1),
    /**
     * 删除
     */
    DELETE(1, 2),
    /**
     * 自动启用
     */
    AUTO_ENABLE(2, 0),
    /**
     * 自动禁用
     */
    AUTO_DISABLE(2, 1),
    /**
     * 上级禁用，关联自动禁用
     */
    PARENT_DISABLE(2, 2);

    byte group;

    byte value;

    static HashMap<Integer, DataStatus> map = new HashMap<Integer, DataStatus>();

    static {
        for (DataStatus value : DataStatus.values()) {
            map.put(value.calculate(), value);
        }
    }

    private DataStatus(int group, int value) {
        this.group = (byte) group;
        this.value = (byte) value;
    }

    public int calculate() {
        return (group << 8) + value;
    }

    public static DataStatus parser(int value) {
        DataStatus status = map.get(value);
        if (status == null) {
            throw new IllegalArgumentException("Status parser error, value=[" + value + "].");
        }
        return status;
    }
}
