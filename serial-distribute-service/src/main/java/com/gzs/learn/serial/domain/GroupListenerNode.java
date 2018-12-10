package com.gzs.learn.serial.domain;

import com.gzs.learn.serial.type.NodeType;

import lombok.Getter;

@Getter
public class GroupListenerNode {

    /**
     * 分组名称
     */
    private String name;

    /**
     * 分组版本
     */
    private int version;

    /**
     * 分组实例时间戳
     */
    private long tsup;

    /**
     * 通知类型
     */
    private NodeType type;

    public GroupListenerNode(String name, int version, long tsup, NodeType type) {
        super();
        this.name = name;
        this.version = version;
        this.tsup = tsup;
        this.type = type;
    }
}
