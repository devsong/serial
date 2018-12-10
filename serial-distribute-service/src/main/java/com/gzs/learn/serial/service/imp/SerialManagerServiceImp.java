package com.gzs.learn.serial.service.imp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gzs.learn.serial.domain.DataStatus;
import com.gzs.learn.serial.domain.GroupListenerNode;
import com.gzs.learn.serial.domain.IndexNode;
import com.gzs.learn.serial.domain.SerialGroup;
import com.gzs.learn.serial.domain.SerialNode;
import com.gzs.learn.serial.domain.SerialPartition;
import com.gzs.learn.serial.repository.SerialGroupRepository;
import com.gzs.learn.serial.repository.SerialPartitionRepository;
import com.gzs.learn.serial.service.SerialDistributeService;
import com.gzs.learn.serial.service.SerialManagerService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SerialManagerServiceImp implements SerialManagerService {
    private static final String NODE_KEY_FORMAT = "%s,%d";

    private static final int CACHE_LENGTH = 10;

    private static final int LIMIT_LENGTH = 8;

    private static final int QUEUE_LENGTH = 10000;

    private HashMap<String, List<Integer>> versionMap = new HashMap<>();

    private HashMap<String, SerialNode> nodeMap = new HashMap<String, SerialNode>();

    private HashMap<String, IndexNode> indexMap = new HashMap<String, IndexNode>();

    private HashMap<String, SerialGroup> groupMap = new HashMap<String, SerialGroup>();

    private LinkedBlockingQueue<GroupListenerNode> queue = new LinkedBlockingQueue<>(QUEUE_LENGTH);

    @Autowired
    private SerialGroupRepository serialGroupRepository;

    @Autowired
    private SerialPartitionRepository serialPartitionRepository;

    @Autowired
    private SerialDistributeService serialDistributeService;

    @Override
    @Transactional
    public void createSerialGroup(String name, int version) {
        SerialGroup group = this.serialGroupRepository.getSerialGroup(name, version);
        if (group == null) {
            log.error("unknow serial group name {},version {}", name, version);
            return;
        }
        if (group.getStat() != DataStatus.ENABLE) {
            log.error("serial group not enable,name=[{}], version=[{}], status=[{}].",
                    group.getName(), group.getVersion(), group.getStat().name());
            return;
        }

        IndexNode index =
                new IndexNode((int) (group.getPartition() * Math.random()), group.getPartition());
        String key = String.format(NODE_KEY_FORMAT, group.getName(), group.getVersion());
        this.indexMap.put(key, index);
        this.groupMap.put(key, group);
        List<Integer> list = this.versionMap.get(name);
        if (list == null) {
            list = new ArrayList<Integer>();
            this.versionMap.put(name, list);
        }
        list.add(version);
        this.registNode(group, index.getIndex());
    }

    @Override
    @Transactional
    public void rechargeSerialGroup(String name, int version, long tsup) {
        String key = String.format(NODE_KEY_FORMAT, name, version);
        SerialNode node = nodeMap.get(key);
        SerialGroup group = groupMap.get(key);
        IndexNode index = indexMap.get(key);
        if (node == null || group == null || index == null) {
            throw new IllegalArgumentException("Primary key is already exist!");
        }
        if (node.checkRemainder()) {
            for (int i = 0; i < group.getPartition(); i++) {
                try {
                    this.registNode(group, index.getIndex());
                    break;
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
            node.finishRecharge();
        }
    }

    public void registNode(SerialGroup group, int index) {
        SerialPartition partition = this.serialPartitionRepository
                .getSerialPartition(group.getName(), group.getVersion(), index);
        if (partition == null) {
            throw new IllegalArgumentException("Serial partition is null");
        }
        if (partition.getUsed() >= partition.getMax()) {
            throw new IllegalArgumentException("Serial partition is full.");
        }

        // 已使用位置小于分区位置，表示分区还未使用
        long offset =
                partition.getUsed() > group.getMin() ? partition.getUsed() + 1 : group.getMin();
        long max = offset + group.getStep() * CACHE_LENGTH - 1;
        long limit = offset + group.getStep() * LIMIT_LENGTH - 1;

        if (max > group.getMax()) {
            max = group.getMax();
            limit = max;
        }

        SerialNode node = new SerialNode(group.getName(), group.getVersion(),
                System.currentTimeMillis(), offset, max, group.getStep(), limit, this.queue);
        if (this.serialPartitionRepository.updateSerialPartition(group.getName(),
                group.getVersion(), index, max, partition.getUsed())) {
            this.serialDistributeService.regist(group.getName(), node);
            this.nodeMap.put(String.format(NODE_KEY_FORMAT, group.getName(), group.getVersion()),
                    node);
        } else {
            throw new IllegalArgumentException(
                    String.format("Regist serial on databases error,name=[%s], version=[%d].",
                            group.getName(), group.getVersion()));
        }
    }

    @Override
    @Transactional
    public long directSerialGroup(String name, int length) {
        long ret = 0L;
        List<Integer> list = this.versionMap.get(name);

        for (Integer version : list) {
            String key = String.format(NODE_KEY_FORMAT, name, version);
            SerialGroup group = groupMap.get(key);
            IndexNode index = indexMap.get(key);
            if (group == null || index == null) {
                continue;
            }
            for (int i = 0; i < group.getPartition(); i++) {
                int part_idx = index.getIndex();
                SerialPartition partition =
                        this.serialPartitionRepository.getSerialPartition(name, version, part_idx);
                if (partition.getUsed() + length > partition.getMax()) {
                    continue;
                }
                if (this.serialPartitionRepository.updateSerialPartition(name, version, part_idx,
                        partition.getUsed() + length, partition.getUsed())) {
                    ret = partition.getUsed() + 1;
                    log.info("direct get serial name=[{}], version=[{}], partition=[{}].", name,
                            version, part_idx);
                    break;
                } else {
                    continue;
                }
            }
            if (ret > 0) {
                break;
            }
        }

        return ret;
    }

    public void removeNode(String name, int version, long tsup) {
        if (tsup > 0L) {
            this.serialDistributeService.unregist(name, version, tsup);
        } else {
            List<Integer> list = this.versionMap.get(name);
            int del = -1;
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) == version) {
                        del = i;
                        break;
                    }
                }
                if (del > 0) {
                    list.remove(del);
                }
            }
            String key = String.format(NODE_KEY_FORMAT, name, version);
            this.groupMap.remove(key);
            this.nodeMap.remove(key);

        }
    }

    /**
     * @return the operatorQueue
     */
    @Override
    public LinkedBlockingQueue<GroupListenerNode> getOperatorQueue() {
        return queue;
    }

}
