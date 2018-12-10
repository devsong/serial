package com.gzs.learn.serial.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class SerialPartition implements Serializable {
    /**
     * 
     */
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
     * 分区编号
     */
    private int partition;

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
     * 分组使用位置
     */
    private long used;
    /**
     * 创建时间
     */
    private Date tscr;
}
