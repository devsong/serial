package com.gzs.learn.serial.service;

import com.gzs.learn.serial.domain.SerialGroup;
import com.gzs.learn.serial.domain.pk.SerialGroupPK;
import com.gzs.learn.serial.exception.SerialException;

public interface SerialManagerService {
    /**
     * 创建分组
     * 
     * @param group
     * @return
     * @throws SerialException
     */
    SerialGroupPK createSerialGroup(SerialGroup group) throws SerialException;

    void init();

}
