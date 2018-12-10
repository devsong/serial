package com.gzs.learn.serial.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gzs.learn.serial.domain.GroupListenerNode;
import com.gzs.learn.serial.service.SerialDistributeService;
import com.gzs.learn.serial.service.SerialManagerService;
import com.gzs.learn.serial.service.SerialUpdateService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SerialUpdateServiceImp implements SerialUpdateService, Runnable {
    @Autowired
    private SerialManagerService serialManagerService;

    @Autowired
    private SerialDistributeService serialDistributeService;

    @Override
    public void init() {
        new Thread(this).start();
    }

    @Override
    public void addQueue(GroupListenerNode node) {
        try {
            this.serialManagerService.getOperatorQueue().put(node);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                GroupListenerNode node = this.serialManagerService.getOperatorQueue().take();
                try {
                    switch (node.getType()) {
                        case ADD:
                            this.serialManagerService.createSerialGroup(node.getName(),
                                    node.getVersion());
                            break;
                        case REMOVE:
                            this.serialDistributeService.unregist(node.getName(), node.getVersion(),
                                    node.getTsup());
                            break;
                        case RECHARGE:
                            this.serialManagerService.rechargeSerialGroup(node.getName(),
                                    node.getVersion(), node.getTsup());
                            break;
                        default:
                            log.warn(String.format("Not support type [].", node.getType().name()));
                            break;
                    }
                } catch (IllegalArgumentException e) {
                    log.error("process primary key error, type=[{}], name=[{}], version=[{}]",
                            node.getType().name(), node.getName(), node.getVersion(), e);
                }
            }
        } catch (InterruptedException e) {
            log.error("handle queue interrupt", e);
        }
    }
}
