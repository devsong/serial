package com.gzs.learn.serial.domain.pk;

import java.io.Serializable;

import lombok.Data;

@Data
public class SerialGroupPK implements Serializable {
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

    public SerialGroupPK() {

    }

    public SerialGroupPK(String name, int version) {
        this.name = name;
        this.version = version;
    }
}
