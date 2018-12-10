package com.gzs.learn.serial.service;

import com.gzs.learn.serial.domain.GroupListenerNode;

public interface SerialUpdateService {

    void addQueue(GroupListenerNode node);

    void init();
}
