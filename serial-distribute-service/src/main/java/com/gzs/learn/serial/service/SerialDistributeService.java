package com.gzs.learn.serial.service;

import com.gzs.learn.serial.domain.SerialNode;

public interface SerialDistributeService {

    /**
     * 获取序列号
     * 
     * @param key
     * @param length
     * @return
     */
    public long getSerial(String key, int length);

    /**
     * 注册序列号节点
     * 
     * @param key
     * @param node
     */
    public void regist(String key, SerialNode node);

    /**
     * 注销序列号节点
     * 
     * @param key
     * @param version
     * @param tsup
     */
    public void unregist(String key, int version, long tsup);
}
