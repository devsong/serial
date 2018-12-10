package com.gzs.learn.serial.dubbo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gzs.learn.serial.domain.SerialGroup;
import com.gzs.learn.serial.domain.pk.SerialGroupPK;
import com.gzs.learn.serial.exception.SerialCode;
import com.gzs.learn.serial.exception.SerialException;
import com.gzs.learn.serial.service.SerialManagerService;
import com.gzs.learn.serial.service.ZookeeperNotifyService;

import lombok.extern.slf4j.Slf4j;

@Component("dubboSerialManagerService")
@Slf4j
public class DubboSerialManagerServiceImp implements DubboSerialManagerService {
    @Autowired
    private SerialManagerService serialManagerService;
    @Autowired
    private ZookeeperNotifyService zookeeperNotifyService;

    @Override
    public void createSerialGroup(SerialGroup group) throws SerialException {
        SerialGroupPK pk = this.serialManagerService.createSerialGroup(group);

        if (!this.zookeeperNotifyService.ceateNode(pk)) {
            log.error("send primary key to zk error {}", pk);
            throw new SerialException(SerialCode.SERIAL_CREATE_ERROR);
        }
    }
}
