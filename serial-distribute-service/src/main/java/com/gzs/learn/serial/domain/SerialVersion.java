package com.gzs.learn.serial.domain;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SerialVersion {
    private String key;

    /**
     * 序列号获取节点
     */
    private SerialNode[] nodes;

    /**
     * 随机位置
     */
    private int index;

    /**
     * 更新节点锁
     */
    private Lock lock = new ReentrantLock();

    /**
     * @param node
     */
    public SerialVersion(String key, SerialNode node) {
        this.nodes = new SerialNode[1];
        this.nodes[0] = node;
    }

    /**
     * @param node
     */
    public void addNode(SerialNode node) {
        if (node == null) {
            throw new IllegalArgumentException("node must be not null.");
        }

        try {
            this.lock.lock();
            SerialNode[] newNodes = new SerialNode[nodes.length + 1];
            System.arraycopy(this.nodes, 0, newNodes, 0, this.nodes.length);
            newNodes[this.nodes.length] = node;
            this.nodes = newNodes;
            log.info(String.format("Regist serial success,name=[%s], version=[%d], tsup=[%d].",
                    node.getKey(), node.getVersion(), node.getTsup()));
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * @param key
     * @param version
     * @param tsup
     * @return
     */
    public boolean removeNode(String key, int version, long tsup) {
        boolean ret = false;
        try {
            this.lock.lock();
            int length = this.nodes.length;
            SerialNode[] newNodes = new SerialNode[length];
            System.arraycopy(this.nodes, 0, newNodes, 0, length);
            for (int index = 0; index < length;) {
                SerialNode node = newNodes[index];
                if (node.equal(key, version, tsup)) {
                    if (index + 1 <= length) {
                        System.arraycopy(newNodes, index + 1, newNodes, index, length - index - 1);
                        log.info("unregist serial success,name=[{}], version=[{}], tsup=[{}].", key,
                                version, node.getTsup());
                    }
                    length--;
                } else {
                    index++;
                }
            }
            if (length == 0) {
                ret = true;
            } else {
                SerialNode[] temp = new SerialNode[length];
                System.arraycopy(newNodes, 0, temp, 0, length);
                this.nodes = temp;
            }
        } finally {
            this.lock.unlock();
        }
        return ret;
    }

    /**
     * @param key
     * @param version
     * @return
     */
    public boolean removeNode(String key, int version) {
        boolean ret = false;
        try {
            this.lock.lock();
            int length = this.nodes.length;
            SerialNode[] newNodes = new SerialNode[length];
            System.arraycopy(this.nodes, 0, newNodes, 0, length);
            for (int index = 0; index < length;) {
                SerialNode node = newNodes[index];
                if (node.equal(key, version)) {
                    if (index + 1 <= length) {
                        System.arraycopy(newNodes, index + 1, newNodes, index, length - index - 1);
                        log.info("unregist serial success,name=[{}], version=[{}], tsup=[{}].", key,
                                version, node.getTsup());
                    }
                    length--;
                } else {
                    index++;
                }
            }
            if (length == 0) {
                ret = true;
            } else {
                SerialNode[] temp = new SerialNode[length];
                System.arraycopy(newNodes, 0, temp, 0, length);
                this.nodes = temp;
            }
        } finally {
            this.lock.unlock();
        }
        return ret;
    }

    /**
     * @param length
     * @return
     */
    public long getSerial(int length) {
        long ret = 0L;
        SerialNode[] temps = this.nodes;
        if (temps != null) {
            int ti = this.index;
            int size = temps.length;

            for (int i = 0; i < size; i++) {
                ti = (ti + 1) % size;
                ret = temps[ti].getSerial(length);
                if (ret > 0L) {
                    break;
                }
            }
            this.index = ti;
        } else {
            log.error("serial nodes is empty,key=[{}].", this.key);
        }
        return ret;
    }
}
