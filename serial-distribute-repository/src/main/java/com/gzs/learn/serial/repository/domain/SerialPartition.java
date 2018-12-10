package com.gzs.learn.serial.repository.domain;

import java.io.Serializable;

import com.gzs.learn.serial.domain.DataStatus;

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
     * 分组位置
     */
    private long position;

    /**
     * 分组长度
     */
    private long length;
}
