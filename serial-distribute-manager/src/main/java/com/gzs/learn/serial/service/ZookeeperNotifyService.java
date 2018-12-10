package com.gzs.learn.serial.service;

import com.gzs.learn.serial.domain.pk.SerialGroupPK;

/**
 * @author miaowenlong
 *
 */
public interface ZookeeperNotifyService {

    /**
     * @param primaryKey
     * @return
     */
    public boolean ceateNode(SerialGroupPK primaryKey);

    /**
     * @param primaryKey
     * @return
     */
    public boolean deleteNode(SerialGroupPK primaryKey);
}
