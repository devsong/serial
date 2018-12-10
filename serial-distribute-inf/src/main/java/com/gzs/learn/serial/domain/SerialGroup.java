package com.gzs.learn.serial.domain;

import java.io.Serializable;
import java.util.Date;

import com.gzs.learn.serial.exception.SerialCode;
import com.gzs.learn.serial.exception.SerialException;

import lombok.Data;

@Data
public class SerialGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 分组版本
     */
    private int version;

    /**
     * 分组状态
     */
    private DataStatus stat;

    /**
     * 分组最小值
     */
    private long min;

    /**
     * 分组最大值
     */
    private long max;

    /**
     * 分区数量
     */
    private int partition;

    /**
     * 步长
     */
    private int step;

    /**
     * 变更用户标识
     */
    private long upid;

    /**
     * 变更时间戳
     */
    private long tsup;
    /**
     * 创建时间
     */
    private Date tscr;

    public void check() {
        if (this.name.isEmpty() || this.name.length() > 32) {
            throw new SerialException(SerialCode.SERIAL_NAME_LEN_TOO_LONG, name);
        }

        if (this.version < 0) {
            throw new SerialException(SerialCode.SERIAL_VERSION_ERROR, version + "");
        }

        if (max <= 0 || min <= 0 || min >= max) {
            throw new SerialException(SerialCode.SERIAL_RANGE_ERROR, "min:" + min + " max:" + max);
        }

        if (this.partition <= 8) {
            throw new SerialException(SerialCode.SERIAL_PARTITION_ERROR, partition + "");
        }

        if (this.step < 10000) {
            throw new IllegalArgumentException(
                    "Serial Group version error, version must be > 10000.");
        }
    }
}
