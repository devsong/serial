package com.gzs.learn.serial.dubbo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gzs.learn.serial.service.SerialDistributeService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component("dubboSerialDistributeService")
public class DubboSerialDistributeServiceImp implements DubboSerialDistributeService {
    @Autowired
    private SerialDistributeService serialDistributeService;

    /**
     * 获取序列号
     *
     * @param key
     * @param length
     * @return
     */
    @Override
    public long getSerial(String key, int length) {
        try {
            return this.serialDistributeService.getSerial(key, length);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
