package com.gzs.learn.serial.dubbo;

import com.gzs.learn.serial.domain.SerialGroup;
import com.gzs.learn.serial.exception.SerialException;

public interface DubboSerialManagerService {
    void createSerialGroup(SerialGroup group) throws SerialException;
}
