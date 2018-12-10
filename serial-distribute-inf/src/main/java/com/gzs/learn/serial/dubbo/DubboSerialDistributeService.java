package com.gzs.learn.serial.dubbo;

public interface DubboSerialDistributeService {

    /**
     * 获取序列号
     * 
     * @param key
     * @param length
     * @return
     */
    public long getSerial(String key, int length);
}
