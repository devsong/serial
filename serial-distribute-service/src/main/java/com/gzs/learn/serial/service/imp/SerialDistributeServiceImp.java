package com.gzs.learn.serial.service.imp;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gzs.learn.serial.domain.SerialNode;
import com.gzs.learn.serial.domain.SerialVersion;
import com.gzs.learn.serial.service.SerialDistributeService;
import com.gzs.learn.serial.service.SerialManagerService;

@Service
public class SerialDistributeServiceImp implements SerialDistributeService {
    private HashMap<String, SerialVersion> serialCache = new HashMap<>();

    @Autowired
    private SerialManagerService serialManagerService;

    @Override
    public long getSerial(String key, int length) {
        SerialVersion node = serialCache.get(key);
        if (node == null) {
            throw new IllegalArgumentException(String.format("Can not find serial,key=[%s].", key));
        }

        long serial = node.getSerial(length);
        if (serial == 0) {
            serial = this.serialManagerService.directSerialGroup(key, length);
        }
        return serial;
    }

    @Override
    public void regist(String key, SerialNode node) {
        SerialVersion version = this.serialCache.get(key);
        if (version == null) {
            version = new SerialVersion(key, node);
            this.serialCache.put(key, version);
        }
        version.addNode(node);
    }

    @Override
    public void unregist(String key, int version, long tsup) {
        SerialVersion ver = this.serialCache.get(key);
        if (ver != null) {
            if (ver.removeNode(key, version, tsup)) {
                this.serialCache.remove(key);
            }
        }
    }
}
