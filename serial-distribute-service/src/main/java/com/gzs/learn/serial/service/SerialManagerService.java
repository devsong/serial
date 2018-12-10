package com.gzs.learn.serial.service;

import java.util.concurrent.LinkedBlockingQueue;

import com.gzs.learn.serial.domain.GroupListenerNode;

/**
 * @author miaowenlong
 *
 */
public interface SerialManagerService {

    void rechargeSerialGroup(String name, int version, long tsup);

    void createSerialGroup(String name, int version);

    LinkedBlockingQueue<GroupListenerNode> getOperatorQueue();

    long directSerialGroup(String name, int length);

}
