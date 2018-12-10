package com.gzs.learn.serial.service.imp;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gzs.learn.serial.service.SerialUpdateService;
import com.gzs.learn.serial.service.ZookeeperNotifyService;

@Service
public class InitService {
    @Autowired
    private SerialUpdateService serialUpdateService;

    @Autowired
    private ZookeeperNotifyService zookeeperNotifyService;

    @PostConstruct
    public void init() throws Exception {
        this.serialUpdateService.init();
        this.zookeeperNotifyService.init(this.serialUpdateService);
    }
}
