package com.gzs.learn.serial.service;

public interface ZookeeperNotifyService {
    void init(SerialUpdateService serialUpdateService) throws Exception;

    boolean tryLock(String name, int version, int partition);
}
