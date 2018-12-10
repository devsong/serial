package com.gzs.learn.serial.repository.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class SerialPartitionUsed implements Serializable {
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
     * 已使用位置
     */
    private long upos;

    /**
     * 最后更新时间
     */
    private long tsup;
}
