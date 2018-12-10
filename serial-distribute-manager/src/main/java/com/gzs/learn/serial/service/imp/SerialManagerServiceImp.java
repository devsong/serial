package com.gzs.learn.serial.service.imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gzs.learn.serial.domain.SerialGroup;
import com.gzs.learn.serial.domain.SerialPartition;
import com.gzs.learn.serial.domain.pk.SerialGroupPK;
import com.gzs.learn.serial.exception.SerialCode;
import com.gzs.learn.serial.exception.SerialException;
import com.gzs.learn.serial.repository.SerialGroupRepository;
import com.gzs.learn.serial.repository.SerialPartitionRepository;
import com.gzs.learn.serial.service.SerialManagerService;
import com.gzs.learn.serial.service.ZookeeperNotifyService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SerialManagerServiceImp implements SerialManagerService {

    @Autowired
    private SerialGroupRepository serialGroupRepository;
    @Autowired
    private SerialPartitionRepository serialPartitionRepository;
    @Autowired
    private ZookeeperNotifyService zookeeperNotifyService;

    @Override
    public void init() {
        List<SerialGroupPK> primaryKeys = this.serialGroupRepository.getEnableSerialGroupsName();
        for (SerialGroupPK pk : primaryKeys) {
            if (this.zookeeperNotifyService.ceateNode(pk)) {
                log.info("create node [{}_{}] success!", pk.getName(), pk.getVersion());
            } else {
                log.info("create node [{}_{}] failed!", pk.getName(), pk.getVersion());
            }
        }
    }

    private void createSerialPartition(SerialGroup group) throws SerialException {
        long length = (group.getMax() - group.getMin()) / group.getPartition();
        long min = group.getMin() - 1;
        long max = min + length;
        for (int index = 0; index < group.getPartition(); index++) {
            SerialPartition partition = new SerialPartition();
            partition.setName(group.getName());
            partition.setPartition(index);
            partition.setVersion(group.getVersion());
            partition.setStat(group.getStat());
            partition.setMin(min + index * length + 1);
            partition.setMax(max + index * length);
            partition.setUsed(partition.getMin());
            if (!this.serialPartitionRepository.registSerialPartition(partition)) {
                throw new SerialException(SerialCode.SERIAL_PARTITION_ERROR);
            }
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SerialGroupPK createSerialGroup(SerialGroup group) throws SerialException {
        try {
            group.check();
            int maxVersion = findMaxVersion(group);
            if (group.getVersion() == 0) {
                group.setVersion(maxVersion + 1);
            }

            if (!this.serialGroupRepository.registSerialGroups(group)) {
                throw new SerialException(SerialCode.SERIAL_CREATE_ERROR);
            }
            this.createSerialPartition(group);
            return new SerialGroupPK(group.getName(), group.getVersion());
        } catch (Exception e) {
            log.error("create serial error", e);
            throw e;
        }
    }

    private int findMaxVersion(SerialGroup group) {
        List<SerialGroup> list = this.serialGroupRepository.getSerialGroups(group.getName());
        int maxVersion = 0;
        for (SerialGroup version : list) {
            // 相同版本号
            if (version.getVersion() == group.getVersion()) {
                throw new SerialException(SerialCode.SERIAL_ALREADY_EXISTS,
                        String.format("serial already exist,name=[%s],version=[%d].",
                                version.getName(), version.getVersion()));
            }
            // 与已有版本存在区间冲突
            if (!(group.getMax() < version.getMin() || group.getMin() > version.getMax())) {
                throw new SerialException(SerialCode.SERIAL_RANGE_ERROR,
                        String.format("serial range conflict,name=[%s],version=[%d],Range=[%d~%d].",
                                version.getName(), version.getVersion(), version.getMin(),
                                version.getMax()));
            }
            maxVersion = maxVersion > version.getVersion() ? maxVersion : version.getVersion();
        }
        return maxVersion;
    }
}
